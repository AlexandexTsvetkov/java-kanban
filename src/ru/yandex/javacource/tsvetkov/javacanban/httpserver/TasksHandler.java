package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.tsvetkov.javacanban.manager.*;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        Gson gson = getGson();

        try {
            switch (method) {
                case "POST":
                    if (Pattern.matches("^/api/v1/tasks$", path)) {

                        try (InputStream inputStream = httpExchange.getRequestBody()) {

                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Task task = gson.fromJson(body, Task.class);

                            try {

                                if (task.getId() == 0) {

                                    int newId = manager.addNewTask(task);
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("id", newId);
                                    String responceBody = gson.toJson(jsonObject);
                                    sendText(httpExchange, responceBody, 201);

                                } else {

                                    try {
                                        manager.updateTask(task);
                                        sendText(httpExchange, "", 201);
                                    } catch (NotFoundExeption exeption) {
                                        sendNotFound(httpExchange);
                                    }
                                }

                            } catch (TaskValidationException exception) {
                                sendHasInteractions(httpExchange, body);
                            }

                        } catch (IOException exception) {
                            sendBadRequest(httpExchange);
                        }

                    } else {
                        sendNotFound(httpExchange);
                    }
                    break;
                case "GET":
                    if (Pattern.matches("^/api/v1/tasks$", path)) {
                        List<Task> tasks = manager.getTasks();
                        String jsonTasks = gson.toJson(tasks);
                        sendText(httpExchange, jsonTasks, 200);
                    } else if (Pattern.matches("^/api/v1/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {

                            try {
                                Task task = manager.getTask(id);
                                String jsonTask = gson.toJson(task);
                                sendText(httpExchange, jsonTask, 200);
                            } catch (NotFoundExeption exeption) {
                                sendNotFound(httpExchange);
                            }

                        } else {
                            sendBadRequest(httpExchange);
                        }
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/api/v1/tasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/tasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {

                            try {
                                manager.removeTask(id);
                                sendText(httpExchange, "", 200);
                            } catch (NotFoundExeption exeption) {
                                sendNotFound(httpExchange);
                            }

                        } else {
                            sendBadRequest(httpExchange);
                        }
                    }
                    break;
                default:
                    sendBadRequest(httpExchange);
            }
        } catch (RuntimeException exception) {
            sendInternalError(httpExchange);
        }
    }
}