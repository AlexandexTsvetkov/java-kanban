package ru.yandex.javacource.tsvetkov.javacanban.task;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void epicsAreEqualWhenIdsAreEqual() {
        ArrayList<Integer> epic1Subtasks = new ArrayList<>();
        epic1Subtasks.add(2);
        Epic epic1 = new Epic("Test1", "Test1", 1, epic1Subtasks);

        ArrayList<Integer> epic2Subtasks = new ArrayList<>();
        epic2Subtasks.add(3);
        epic2Subtasks.add(4);
        Epic epic2 = new Epic("Test2", "Test2", 1, epic2Subtasks);

        assertEquals(epic2, epic1, "Эпики не равны");
    }
}