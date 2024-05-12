package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    static TaskManager taskManager;
    static HistoryManager historyManager;
    static TaskManager fileBackedTaskManager;

    @BeforeEach
    void beforeAll() {
        taskManager = new InMemoryTaskManager();
        historyManager = Managers.getDefaultHistory();
        fileBackedTaskManager = Managers.getDefault();
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