package ru.yandex.javacource.tsvetkov.javacanban.task;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void subtasksAreEqualWhenIdsAreEqual() {
        Subtask subtask1 = new Subtask("Test1", "Test1", 1, Status.NEW, 2, LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));

        Subtask subtask2 = new Subtask("Test2", "Test2", 1, Status.NEW, 3, LocalDateTime.of(2024, 1, 2, 0, 0), Duration.ofDays(1));

        assertEquals(subtask2, subtask1, "Сабтаски не равны");
    }
}