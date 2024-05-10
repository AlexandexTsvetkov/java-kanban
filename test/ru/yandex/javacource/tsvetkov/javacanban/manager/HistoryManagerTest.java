package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HistoryManagerTest {

    public static HistoryManager historyManager;

    @BeforeEach
    void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Test", "Test", 1, Status.NEW);

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addTwoTimes() {
        Task task1 = new Task("Test", "Test", 1, Status.NEW);
        Task task2 = new Task("Test", "Test", task1.getId(), task1.getStatus());

        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не добавилась дважды.");
    }

    @Test
    void addToTheEnd() {
        Task task1 = new Task("Test", "Test", 1, Status.NEW);
        Task task2 = new Task("Test", "Test", task1.getId(), task1.getStatus());
        Epic epic = new Epic("Test", "Test", 2, new ArrayList<>());

        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Задача не добавилась дважды.");
        assertEquals(task1, history.getLast(), "Задача найдена и перемещена в конец");
    }
}