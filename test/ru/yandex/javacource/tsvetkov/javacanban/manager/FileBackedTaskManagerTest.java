package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends InMemoryTaskManagerTest {

    static File emptyFile = new File("resources/EmptyFile.txt");
    static File tempFile;

    @BeforeEach
    void BeforeEach() {

        try {
            if (tempFile != null && tempFile.exists()) {
                Files.delete(tempFile.toPath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            tempFile = File.createTempFile("tempFile1", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = FileBackedTaskManager.loadFromFile(tempFile);

        taskManager.removeEpics();
        taskManager.removeTasks();

        epic1 = new Epic("Test1", "Test1");
        taskManager.addNewEpic(epic1);
        subtask1 = new Subtask("Test2", "Test2", epic1.getId(), LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));
        task1 = new Task("Test3", "Test3", LocalDateTime.of(2024, 1, 2, 0, 0), Duration.ofDays(1));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewTask(task1);
    }

    @Test
    void loadingEmptyFile() {
        taskManager = FileBackedTaskManager.loadFromFile(emptyFile);

        assertEquals(taskManager.getTasks().size(), 0, "Пустой список задач");
        assertEquals(taskManager.getSubTasks().size(), 0, "Пустой список задач");
        assertEquals(taskManager.getSubTasks().size(), 0, "Пустой список задач");
    }

    @Test
    void SaveEmptyFile() throws IOException {
        taskManager = FileBackedTaskManager.loadFromFile(emptyFile);
        taskManager.removeTasks();

        String textFile = Files.readString(emptyFile.toPath());

        assertEquals(textFile, "id,type,name,status,description,startdate,duration,epic", "Сохранен пустой файл");
    }


    @Test
    void SaveSeveralTasks() {
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertArrayEquals(taskManager.getTasks().toArray(), newTaskManager.getTasks().toArray(), "Задачи загружены");
        assertArrayEquals(taskManager.getSubTasks().toArray(), newTaskManager.getSubTasks().toArray(), "Подзадачи загружены");
        assertArrayEquals(taskManager.getEpics().toArray(), newTaskManager.getEpics().toArray(), "Эпики загружены");
    }

    @Test
    public void testException() {
        assertThrows(ManagerReadException.class, () -> taskManager = FileBackedTaskManager.loadFromFile(new File("resources/ExeptionFile.txt")), "Загрузка из несуществующего файла");
    }
}