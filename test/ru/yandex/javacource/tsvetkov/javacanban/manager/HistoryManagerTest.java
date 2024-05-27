package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
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
        Task task = new Task("Test", "Test", 1, Status.NEW, LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));

        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addTwoTimes() {
        Task task1 = new Task("Test", "Test", 1, Status.NEW, LocalDateTime.of(2024, 1, 2, 0, 0), Duration.ofDays(1));
        Task task2 = new Task("Test", "Test", task1.getId(), task1.getStatus(), LocalDateTime.of(2024, 3, 1, 0, 0), Duration.ofDays(1));

        historyManager.add(task1);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача не добавилась дважды.");
    }

    @Test
    void addToTheEnd() {
        Task task1 = new Task("Test", "Test", 1, Status.NEW, LocalDateTime.of(2024, 1, 4, 0, 0), Duration.ofDays(1));
        Task task2 = new Task("Test", "Test", task1.getId(), task1.getStatus(), LocalDateTime.of(2024, 1, 5, 0, 0), Duration.ofDays(1));
        Epic epic = new Epic("Test", "Test", 2, new ArrayList<>());

        historyManager.add(task1);
        historyManager.add(epic);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Задача не добавилась дважды.");
        assertEquals(task1, history.getLast(), "Задача найдена и перемещена в конец");
    }

    @Test
    void getEmptyHistory() {

        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не пустая");

    }

    @Test
    void removeFromHead() {
        Task task1 = new Task("Test", "Test", 1, Status.NEW, LocalDateTime.of(2024, 1, 4, 0, 0), Duration.ofDays(1));
        Task task2 = new Task("Test", "Test", 2, Status.NEW, LocalDateTime.of(2024, 1, 5, 0, 0), Duration.ofDays(1));
        Epic epic = new Epic("Test", "Test", 3, new ArrayList<>());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic);

        historyManager.remove(1);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая");
    }

    @Test
    void removeFromCenter() {
        Task task1 = new Task("Test", "Test", 1, Status.NEW, LocalDateTime.of(2024, 1, 4, 0, 0), Duration.ofDays(1));
        Task task2 = new Task("Test", "Test", 2, Status.NEW, LocalDateTime.of(2024, 1, 5, 0, 0), Duration.ofDays(1));
        Epic epic = new Epic("Test", "Test", 3, new ArrayList<>());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic);

        historyManager.remove(2);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая");
    }

    @Test
    void removeFromEnd() {
        Task task1 = new Task("Test", "Test", 1, Status.NEW, LocalDateTime.of(2024, 1, 4, 0, 0), Duration.ofDays(1));
        Task task2 = new Task("Test", "Test", 2, Status.NEW, LocalDateTime.of(2024, 1, 5, 0, 0), Duration.ofDays(1));
        Epic epic = new Epic("Test", "Test", 3, new ArrayList<>());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic);

        historyManager.remove(3);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История не пустая");
    }
}