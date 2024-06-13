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
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerHistoryTest {

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
    public void testGetHistory() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        Epic epic = new Epic("Test1", "Test1");

        String epicJson = gson.toJson(epic);

        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofMinutes(5));

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
        assertEquals(201, response3.statusCode());

        URI url4 = URI.create("http://localhost:8080/api/v1/epics/1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response4.statusCode());

        URI url5 = URI.create("http://localhost:8080/api/v1/tasks/2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response5.statusCode());

        URI url6 = URI.create("http://localhost:8080/api/v1/history");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response6.statusCode());

        assertEquals(2, manager.getHistory().size());

        assertEquals(gson.toJson(manager.getHistory()), response6.body());
    }

    @Test
    public void testGetHistoryBadRequest() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        // конвертируем её в JSON
        String task1Json = gson.toJson(task);

        Epic epic = new Epic("Test1", "Test1");

        String epicJson = gson.toJson(epic);

        Subtask subtask = new Subtask("Test 2", "Testing task 2", 1, LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofMinutes(5));

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
        assertEquals(201, response3.statusCode());

        URI url4 = URI.create("http://localhost:8080/api/v1/epics/1");
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response4.statusCode());

        URI url5 = URI.create("http://localhost:8080/api/v1/tasks/2");
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response5.statusCode());

        URI url6 = URI.create("http://localhost:8080/api/v1/history/1");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).GET().build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response6.statusCode());
    }
}