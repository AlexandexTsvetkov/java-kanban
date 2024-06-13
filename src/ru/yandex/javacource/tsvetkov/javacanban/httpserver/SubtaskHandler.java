package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.tsvetkov.javacanban.manager.*;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager manager) {
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
                    if (Pattern.matches("^/api/v1/subtasks$", path)) {

                        try (InputStream inputStream = httpExchange.getRequestBody()) {

                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Subtask subtask = gson.fromJson(body, Subtask.class);

                            try {

                                if (subtask.getId() == 0) {

                                    try {
                                        int newId = manager.addNewSubtask(subtask);
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("id", newId);
                                        String responceBody = gson.toJson(jsonObject);
                                        sendText(httpExchange, responceBody, 201);
                                    } catch (NotFoundExeption exeption) {
                                        sendNotFound(httpExchange);
                                    }

                                } else {

                                    try {
                                        manager.updateSubtask(subtask);
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
                    if (Pattern.matches("^/api/v1/subtasks$", path)) {
                        List<Subtask> subtasks = manager.getSubTasks();
                        String jsonTasks = gson.toJson(subtasks);
                        sendText(httpExchange, jsonTasks, 200);
                    } else if (Pattern.matches("^/api/v1/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {

                            try {
                                Subtask subtask = manager.getSubTask(id);
                                String jsonTask = gson.toJson(subtask);
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
                    if (Pattern.matches("^/api/v1/subtasks/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/subtasks/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {

                            try {
                                manager.removeSubtask(id);
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
        } catch (ManagerSaveException | ManagerReadException exception) {
            sendInternalError(httpExchange);
        }
    }
}