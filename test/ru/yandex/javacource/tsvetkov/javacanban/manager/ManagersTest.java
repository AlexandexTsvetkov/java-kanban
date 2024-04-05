package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    static TaskManager taskManager;
    static HistoryManager historyManager;

    @BeforeAll
    static void beforeAll() {
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