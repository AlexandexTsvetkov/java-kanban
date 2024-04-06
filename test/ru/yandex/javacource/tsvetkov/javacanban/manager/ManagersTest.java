package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    static TaskManager taskManager;
    static HistoryManager historyManager;

    @BeforeEach
    void beforeAll() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void taskManagerIsInitialized() {
        assertNotNull(taskManager);
    }

    @Test
    void historyManagerIsInitialized() {
        assertNotNull(historyManager);
    }
}