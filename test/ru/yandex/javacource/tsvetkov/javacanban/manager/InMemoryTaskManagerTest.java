package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.*;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    static TaskManager taskManager;
    static Epic epic1;
    static Subtask subtask1;
    static Task task1;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @BeforeEach
    void BeforeEach() {

        taskManager.removeEpics();
        taskManager.removeTasks();

        epic1 = new Epic("Test1", "Test1");
        taskManager.addNewEpic(epic1);
        subtask1 = new Subtask("Test2", "Test2", epic1.getId());
        task1 = new Task("Test3", "Test3");

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewTask(task1);
    }

    @Test
    void epicCantBeSubtask() {
//        Epic epic1 = new Epic("Test1", "Test1");
//
//        taskManager.addNewEpic(epic1);
//        taskManager.addNewSubtask(epic1);
    }

    @Test
    void subtaskCantBeEpic() {
//        Subtask subtask = new Subtask("Test1", "Test1");

//        taskManager.addNewEpic(subtask);
//        taskManager.addNewSubtask(subtask);
    }

    @Test
    void taskFieldsAreUnchanged() {
        Task taskUnchanged = new Task("TestUnchanged", "TestUnchanged");

        String nameUnchanged = taskUnchanged.getName();
        String descriptionUnchanged = taskUnchanged.getDescription();
        Status statusUnchanged = taskUnchanged.getStatus();

        assertEquals(nameUnchanged, taskUnchanged.getName(), "Имя не совпадает");
        assertEquals(descriptionUnchanged, taskUnchanged.getDescription(), "Описание не совпадает");
        assertEquals(statusUnchanged, taskUnchanged.getStatus(), "Статус не совпадает");
    }

    @Test
    void subtaskFieldsAreUnchanged() {
        Subtask subtaskUnchanged = new Subtask("TestUnchanged", "TestUnchanged", epic1.getId());

        String nameUnchanged = subtaskUnchanged.getName();
        String descriptionUnchanged = subtaskUnchanged.getDescription();
        Status statusUnchanged = subtaskUnchanged.getStatus();

        assertEquals(nameUnchanged, subtaskUnchanged.getName(), "Имя не совпадает");
        assertEquals(descriptionUnchanged, subtaskUnchanged.getDescription(), "Описание не совпадает");
        assertEquals(statusUnchanged, subtaskUnchanged.getStatus(), "Статус не совпадает");
    }

    @Test
    void epicFieldsAreUnchanged() {
        Epic epicUnchanged = new Epic("TestUnchanged", "TestUnchanged");

        String nameUnchanged = epicUnchanged.getName();
        String descriptionUnchanged = epicUnchanged.getDescription();
        Status statusUnchanged = epicUnchanged.getStatus();

        assertEquals(nameUnchanged, epicUnchanged.getName(), "Имя не совпадает");
        assertEquals(descriptionUnchanged, epicUnchanged.getDescription(), "Описание не совпадает");
        assertEquals(statusUnchanged, epicUnchanged.getStatus(), "Статус не совпадает");
    }

    @Test
    void EpicDoesNotConflict() {
        Epic newEpic = new Epic(epic1.getName(), epic1.getDescription(), epic1.getId(), epic1.getSubtasksId());
        taskManager.updateEpic(newEpic);
        assertEquals(epic1, taskManager.getEpic(newEpic.getId()), "Эпики конфликтуют");
    }

    @Test
    void SubtaskDoesNotConflict() {
        Subtask newSubtask = new Subtask(subtask1.getName(), subtask1.getDescription(), subtask1.getId(), subtask1.getStatus(), subtask1.getEpicId());
        taskManager.updateSubtask(newSubtask);
        assertEquals(subtask1, taskManager.getSubTask(newSubtask.getId()), "Сабтаски конфликтуют");
    }

    @Test
    void TaskDoesNotConflict() {
        Task newTask = new Task(task1.getName(), task1.getDescription(), task1.getId(), task1.getStatus());
        taskManager.updateTask(newTask);
        assertEquals(task1, taskManager.getTask(newTask.getId()), "Задачи конфликтуют");
    }

    @Test
    void historymanagerSavesPreviousVersionOfTask() {

        Task historyTask = taskManager.getTask(task1.getId());

        String name = historyTask.getName();
        String description = historyTask.getDescription();
        Status status = historyTask.getStatus();

        Task newTask = new Task("New name", "New description", task1.getId(), Status.DONE);
        taskManager.updateTask(newTask);
        assertNotEquals(name, taskManager.getTask(task1.getId()).getName());
        assertNotEquals(description, taskManager.getTask(task1.getId()).getDescription());
        assertNotEquals(status, taskManager.getTask(task1.getId()).getStatus());
    }

    @Test
    void historymanagerSavesPreviousVersionOfSubtask() {

        Subtask historySubtask = taskManager.getSubTask(subtask1.getId());

        String name = historySubtask.getName();
        String description = historySubtask.getDescription();
        Status status = historySubtask.getStatus();

        Subtask newSubtask = new Subtask("New name", "New description", subtask1.getId(), Status.DONE, epic1.getId());
        taskManager.updateSubtask(newSubtask);
        assertNotEquals(name, taskManager.getSubTask(subtask1.getId()).getName(), "Старая версия имени");
        assertNotEquals(description, taskManager.getSubTask(subtask1.getId()).getDescription(), "Старая версия описания");
        assertNotEquals(status, taskManager.getSubTask(subtask1.getId()).getStatus(), "Старая версия статуса");
    }

    @Test
    void historymanagerSavesPreviousVersionOfEpic() {

        Epic historyEpic = taskManager.getEpic(epic1.getId());

        String name = historyEpic.getName();
        String description = historyEpic.getDescription();
        Status status = historyEpic.getStatus();
        ArrayList<Integer> subtasksId = historyEpic.getSubtasksId();

        Subtask newSubtask = new Subtask("Some name", "Some description", epic1.getId());

        taskManager.addNewSubtask(newSubtask);

        Epic newEpic = new Epic("New name", "New description", epic1.getId(), taskManager.getEpic(epic1.getId()).getSubtasksId());
        taskManager.updateEpic(newEpic);

        Subtask updatedSubtask = new Subtask("Some name", "Some description", newSubtask.getId(), Status.DONE, epic1.getId());
        taskManager.updateSubtask(updatedSubtask);

        assertNotEquals(name, taskManager.getEpic(epic1.getId()).getName(), "Старая версия имени");
        assertNotEquals(description, taskManager.getEpic(epic1.getId()).getDescription(), "Старая версия описания");
        assertNotEquals(status, taskManager.getEpic(epic1.getId()).getStatus(), "Старая версия статуса");
        assertFalse(Arrays.equals(subtasksId.toArray(), taskManager.getEpic(epic1.getId()).getSubtasksId().toArray()), "Старая версия подзадач");
    }

    @Test
    void addNewTask() {

        int numberOfTasks = taskManager.getTasks().size();

        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(numberOfTasks + 1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getLast(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {

        int numberOfEpics = taskManager.getEpics().size();

        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(numberOfEpics + 1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getLast(), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {

        int numberOfSubtasks = taskManager.getSubTasks().size();

        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description", epic1.getId());
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubTask(subtaskId);

        assertNotNull(savedSubtask, "Сабтаска не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubTasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(numberOfSubtasks + 1, subtasks.size(), "Неверное количество сабтасок.");
        assertEquals(subtask, subtasks.getLast(), "сабтаски не совпадают.");
    }

    @Test
    void removeTasks() {
        taskManager.removeTasks();
        assertEquals(0, taskManager.getTasks().size(), "Задачи не удалены");
    }

    @Test
    void removeEpics() {
        taskManager.removeEpics();
        assertEquals(0, taskManager.getEpics().size(), "Эпики не удалены");
        assertEquals(0, taskManager.getSubTasks().size(), "Сабтаски не удалены");
    }

    @Test
    void removeSubtasks() {
        taskManager.removeSubTasks();
        assertEquals(0, taskManager.getSubTasks().size(), "Эпики не удалены");
    }

    @Test
    void updateEpicStatus() {

        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test new subtask", "Test new subtask", epicId);

        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус не соответствует ожидаемому");

        subtask = new Subtask("Test new subtask", "Test new subtask", subtaskId, Status.IN_PROGRESS, epicId);

        taskManager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус не соответствует ожидаемому");

        subtask = new Subtask("Test new subtask", "Test new subtask", subtaskId, Status.DONE, epicId);

        taskManager.updateSubtask(subtask);

        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус не соответствует ожидаемому");
    }

    @Test
    void getSubtasksOfEpic() {
        Subtask[] subtasks = {subtask1};
        assertArrayEquals(subtasks, taskManager.getSubTasksOfEpic(epic1.getId()).toArray(), "Сабтаски эпика не совпадают");
    }
}