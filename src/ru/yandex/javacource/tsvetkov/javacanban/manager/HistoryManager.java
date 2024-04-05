package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.List;

public interface HistoryManager {

    <T extends Task> T add(T task);

    List<Task> getHistory();
}
