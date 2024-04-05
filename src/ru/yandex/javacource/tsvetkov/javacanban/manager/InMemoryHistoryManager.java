package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    ArrayList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public <T extends Task> T add(T task) {

        if (history.size() == 10) {
            history.removeFirst();
        }
        history.add(task);
        return task;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

}
