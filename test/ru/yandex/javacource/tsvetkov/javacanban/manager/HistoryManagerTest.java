package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    public static HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void add() {
        Task task = new Task("Test", "Test");

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}