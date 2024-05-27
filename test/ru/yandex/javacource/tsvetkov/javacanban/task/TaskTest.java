package ru.yandex.javacource.tsvetkov.javacanban.task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksAreEqualWhenIdsAreEqual() {
        Task task1 = new Task("Test1", "Test1", 1, Status.NEW, LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));

        Task task2 = new Task("Test2", "Test2", 1, Status.IN_PROGRESS, LocalDateTime.of(2024, 1, 2, 0, 0), Duration.ofDays(1));

        assertEquals(task2, task1, "Таски не равны");
    }
}