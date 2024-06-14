package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.manager.Managers;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubtasksTest {

    TaskManager manager;
    HttpTaskServer taskServer;
    Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() {
        manager = Managers.getDefault();
        try {
            taskServer = new HttpTaskServer(manager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtasksFromManager = manager.getSubTasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddSubtaskErrorEpicNotFound() throws IOException, InterruptedException {

        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {

        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        Subtask subtaskUpdate = new Subtask("Test Update", "Testing task 2", 2, Status.DONE, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String subtaskUpdateJson = gson.toJson(subtaskUpdate);

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(HttpRequest.BodyPublishers.ofString(subtaskUpdateJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response3.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtasksFromManager = manager.getSubTasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Update", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtaskErrorNotFound() throws IOException, InterruptedException {

        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        Subtask subtaskUpdate = new Subtask("Test Update", "Testing task 2", 4, Status.DONE, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String subtaskUpdateJson = gson.toJson(subtaskUpdate);

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(HttpRequest.BodyPublishers.ofString(subtaskUpdateJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());
    }

    @Test
    public void testAddTaskErrorNotAcceptable() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        Epic epic = new Epic("Test1", "Test1");

        String epicJson = gson.toJson(epic);

        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));

        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(406, response3.statusCode());

    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks/2");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response3.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        assertEquals(gson.toJson(manager.getSubTask(2)), response3.body(), "Подзадача не возвращается");
    }

    @Test
    public void testGetSubtaskNotFoundError() throws IOException, InterruptedException {
        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks/3");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(404, response3.statusCode());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());
        assertEquals(1, manager.getSubTasks().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks/2");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response3.statusCode());

        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    public void testDeleteSubtaskErrorNotFound() throws IOException, InterruptedException {
        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());
        assertEquals(1, manager.getSubTasks().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks/3");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(404, response3.statusCode());
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);
        String subtaskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/subtasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());
        assertEquals(1, manager.getSubTasks().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/subtasks");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response3.statusCode());
        assertEquals(gson.toJson(manager.getSubTasks()), response3.body());
    }
}