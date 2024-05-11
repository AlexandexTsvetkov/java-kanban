package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    static TaskManager taskManager;
    static HistoryManager historyManager;
    static TaskManager fileBackedTaskManager;

    @BeforeEach
    void beforeAll() throws IOException {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(File.createTempFile("tempFile", ".txt"));
    }

    @Test
    void taskManagerIsInitialized() {
        assertNotNull(taskManager);
    }

    @Test
    void historyManagerIsInitialized() {
        assertNotNull(historyManager);
    }

    @Test
    void fileBackedTaskManagerIsInitialized() {
        assertNotNull(fileBackedTaskManager);
    }
}