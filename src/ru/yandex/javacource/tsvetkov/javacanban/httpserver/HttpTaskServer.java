package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.tsvetkov.javacanban.manager.Managers;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static final int PORT = 8080;
    public final TaskManager manager;
    private final HttpServer httpServer;

    public static void main(String[] args) {

        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer(Managers.getDefault());
            httpTaskServer.start();
            //stop(httpServer);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при запуске Http - сервера");
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Сервер остановлен на порту " + PORT);
    }

    public HttpTaskServer(TaskManager manager) throws IOException {

        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

        this.httpServer.createContext("/api/v1/tasks", new TasksHandler(manager));
        this.httpServer.createContext("/api/v1/subtasks", new SubtaskHandler(manager));
        this.httpServer.createContext("/api/v1/epics", new EpicHandler(manager));
        this.httpServer.createContext("/api/v1/history", new HistoryHandler(manager));
        this.httpServer.createContext("/api/v1/prioritized", new PrioritizedHandler(manager));
        this.manager = manager;
    }
}
