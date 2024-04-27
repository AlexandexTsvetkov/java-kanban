package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
