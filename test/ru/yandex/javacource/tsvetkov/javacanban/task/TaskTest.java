package ru.yandex.javacource.tsvetkov.javacanban.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksAreEqualWhenIdsAreEqual() {
        Task task1 = new Task("Test1", "Test1", 1, Status.NEW);

        Task task2 = new Task("Test2", "Test2", 1, Status.IN_PROGRESS);

        assertEquals(task2, task1, "Таски не равны");
    }
}