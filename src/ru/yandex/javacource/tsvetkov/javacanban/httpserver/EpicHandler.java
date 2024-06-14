package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.tsvetkov.javacanban.manager.*;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager manager) {
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
                    if (Pattern.matches("^/api/v1/epics$", path)) {

                        try (InputStream inputStream = httpExchange.getRequestBody()) {

                            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                            Epic epic = gson.fromJson(body, Epic.class);

                            try {

                                if (epic.getId() == 0) {

                                    int newId = manager.addNewEpic(epic);
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("id", newId);
                                    String responceBody = gson.toJson(jsonObject);
                                    sendText(httpExchange, responceBody, 201);

                                } else {

                                    try {
                                        manager.updateEpic(epic);
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
                    if (Pattern.matches("^/api/v1/epics/\\d+/subtasks$", path)) {
                        String pathId = path.replaceFirst("/api/v1/epics/", "").replaceFirst("/subtasks", "");
                        int id = parsePathId(pathId);

                        if (id != -1) {

                            try {
                                List<Subtask> subtasks = manager.getSubTasksOfEpic(id);
                                String jsonTask = gson.toJson(subtasks);
                                sendText(httpExchange, jsonTask, 200);
                            } catch (NotFoundExeption exeption) {
                                sendNotFound(httpExchange);
                            }

                        } else {
                            sendBadRequest(httpExchange);
                        }
                    } else if (Pattern.matches("^/api/v1/epics$", path)) {
                        List<Epic> epics = manager.getEpics();
                        String jsonTasks = gson.toJson(epics);
                        sendText(httpExchange, jsonTasks, 200);
                    } else if (Pattern.matches("^/api/v1/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {

                            try {
                                Epic epic = manager.getEpic(id);
                                String jsonTask = gson.toJson(epic);
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
                    if (Pattern.matches("^/api/v1/epics/\\d+$", path)) {
                        String pathId = path.replaceFirst("/api/v1/epics/", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {

                            try {
                                manager.removeEpic(id);
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