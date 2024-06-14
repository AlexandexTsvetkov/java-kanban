package ru.yandex.javacource.tsvetkov.javacanban.httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.tsvetkov.javacanban.manager.TaskManager;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();
        Gson gson = getGson();

        if (method.equals("GET")) {
            if (Pattern.matches("^/api/v1/prioritized$", path)) {
                List<Task> prioritizedTasks = manager.getPrioritizedTasks();
                String jsonTasks = gson.toJson(prioritizedTasks);
                sendText(httpExchange, jsonTasks, 200);
            } else {
                sendBadRequest(httpExchange);
            }
        } else {
            sendBadRequest(httpExchange);
        }
    }
}