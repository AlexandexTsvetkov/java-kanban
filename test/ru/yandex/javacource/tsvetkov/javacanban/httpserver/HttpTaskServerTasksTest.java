package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.manager.Managers;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
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

public class HttpTaskServerTasksTest {

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
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        Task taskUpdate = new Task("Test update", "Testing task 2", 1, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskUpdateJson = gson.toJson(taskUpdate);

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskUpdateJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test update", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskErrorNotAcceptable() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        Task task2 = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));

        String task2Json = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = manager.getTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        // проверяем код ответа
        assertEquals(406, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
    }

    @Test
    public void testUpdateTaskErrorNotFound() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        Task taskUpdate = new Task("Test update", "Testing task 2", 2, Status.DONE, LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String taskUpdateJson = gson.toJson(taskUpdate);

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskUpdateJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks/1");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        assertEquals(gson.toJson(manager.getTask(1)), response2.body(), "Задача не возвращается");
    }

    @Test
    public void testGetTaskNotFoundError() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks/2");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        assertEquals(1, manager.getTasks().size(), "Задача не добавлена");

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks/1");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(200, response2.statusCode());

        // проверяем, что создалась одна задача с корректным именем

        assertEquals(0, manager.getTasks().size(), "Задача не удалена");
    }

    @Test
    public void testDeleteTaskErrorNotFound() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks/2");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/api/v1/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response1.statusCode());

        URI url2 = URI.create("http://localhost:8080/api/v1/tasks");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        assertEquals(1, manager.getTasks().size(), "Некорректное количество задач");

        // проверяем код ответа
        assertEquals(gson.toJson(manager.getTasks()), response2.body(), "Задачи не возвращается");
    }
} 