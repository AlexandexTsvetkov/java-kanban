package ru.yandex.javacource.tsvetkov.javacanban.manager;

import java.io.File;

public final class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager(new File("resources/manager.txt"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
