package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.manager.Managers;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerEpicsTest {

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
    public void testAddEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test1", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {

        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        Epic epicUpdate = new Epic("Test Update", "Test1", 1, new ArrayList<>());
        // создаём задачу
        // конвертируем её в JSON
        String epicUpdateJson = gson.toJson(epicUpdate);

        URI url2 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(epicUpdateJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Update", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpicErrorNotFound() throws IOException, InterruptedException {

        Epic epic = new Epic("Test1", "Test1");
        // создаём задачу
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        Epic epicUpdate = new Epic("Test Update", "Test1", 5, new ArrayList<>());
        // создаём задачу
        // конвертируем её в JSON
        String epicUpdateJson = gson.toJson(epicUpdate);

        URI url2 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(epicUpdateJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test1", "Test1");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/epics/1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response2.statusCode());
        assertEquals(gson.toJson(manager.getEpic(1)), response2.body(), "Эпик не возвращается");
    }

    @Test
    public void testGetEpicNotFoundError() throws IOException, InterruptedException {

        Epic epic = new Epic("Test1", "Test1");
        // конвертируем её в JSON
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();

        URI url1 = URI.create("http://localhost:8080/api/v1/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/epics/2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {

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
        assertEquals(1, manager.getEpics().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/epics/1");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response3.statusCode());

        assertEquals(0, manager.getSubTasks().size());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void testDeleteEpicErrorNotFound() throws IOException, InterruptedException {

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
        assertEquals(1, manager.getEpics().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/epics/3");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(404, response3.statusCode());
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {

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
        assertEquals(1, manager.getEpics().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/epics");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());
        // проверяем код ответа
        assertEquals(gson.toJson(manager.getEpics()), response3.body());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {

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
        assertEquals(1, manager.getEpics().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/epics/1/subtasks");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode());
        // проверяем код ответа
        assertEquals(gson.toJson(manager.getSubTasksOfEpic(1)), response3.body());
    }

    @Test
    public void testGetEpicSubtasksErrorNotFound() throws IOException, InterruptedException {

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
        assertEquals(1, manager.getEpics().size());

        URI url3 = URI.create("http://localhost:8080/api/v1/epics/3/subtasks");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response3.statusCode());
    }
}