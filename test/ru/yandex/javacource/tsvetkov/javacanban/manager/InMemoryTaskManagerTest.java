package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void BeforeEach() {
        taskManager = new InMemoryTaskManager();

        taskManager.removeEpics();
        taskManager.removeTasks();

        epic1 = new Epic("Test1", "Test1");
        taskManager.addNewEpic(epic1);
        subtask1 = new Subtask("Test2", "Test2", epic1.getId(), LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));
        task1 = new Task("Test3", "Test3", LocalDateTime.of(2024, 1, 2, 0, 0), Duration.ofDays(1));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewTask(task1);
    }

    @Override
    @Test
    void epicCantBeSubtask() {
//        Epic epic1 = new Epic("Test1", "Test1");
//
//        taskManager.addNewEpic(epic1);
//        taskManager.addNewSubtask(epic1);
    }

    @Override
    @Test
    void subtaskCantBeEpic() {
//        Subtask subtask = new Subtask("Test1", "Test1");

//        taskManager.addNewEpic(subtask);
//        taskManager.addNewSubtask(subtask);
    }

    @Override
    @Test
    void taskFieldsAreUnchanged() {
        Task taskUnchanged = new Task("TestUnchanged", "TestUnchanged", LocalDateTime.of(2024, 1, 3, 0, 0), Duration.ofDays(1));

        String nameUnchanged = taskUnchanged.getName();
        String descriptionUnchanged = taskUnchanged.getDescription();
        Status statusUnchanged = taskUnchanged.getStatus();

        assertEquals(nameUnchanged, taskUnchanged.getName(), "Имя не совпадает");
        assertEquals(descriptionUnchanged, taskUnchanged.getDescription(), "Описание не совпадает");
        assertEquals(statusUnchanged, taskUnchanged.getStatus(), "Статус не совпадает");
    }

    @Override
    @Test
    void epicIsExists() {

        Epic testEpic = null;

        if (taskManager.getEpic(subtask1.getEpicId()).isPresent()) {
            testEpic = taskManager.getEpic(subtask1.getEpicId()).get();
        }
        assertNotNull(testEpic, "Эпик не существует");

    }

    @Override
    @Test
    void subtaskFieldsAreUnchanged() {
        Subtask subtaskUnchanged = new Subtask("TestUnchanged", "TestUnchanged", epic1.getId(), LocalDateTime.of(2024, 1, 4, 0, 0), Duration.ofDays(1));

        String nameUnchanged = subtaskUnchanged.getName();
        String descriptionUnchanged = subtaskUnchanged.getDescription();
        Status statusUnchanged = subtaskUnchanged.getStatus();

        assertEquals(nameUnchanged, subtaskUnchanged.getName(), "Имя не совпадает");
        assertEquals(descriptionUnchanged, subtaskUnchanged.getDescription(), "Описание не совпадает");
        assertEquals(statusUnchanged, subtaskUnchanged.getStatus(), "Статус не совпадает");
    }

    @Override
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

    @Override
    @Test
    void EpicDoesNotConflict() {
        Epic newEpic = new Epic(epic1.getName(), epic1.getDescription(), epic1.getId(), epic1.getSubtasksId());
        taskManager.updateEpic(newEpic);
        assertEquals(epic1, taskManager.getEpic(newEpic.getId()).orElseThrow(), "Эпики конфликтуют");
    }

    @Override
    @Test
    void SubtaskDoesNotConflict() {
        Subtask newSubtask = new Subtask(subtask1.getName(), subtask1.getDescription(), subtask1.getId(), subtask1.getStatus(), subtask1.getEpicId(), LocalDateTime.of(2024, 1, 5, 0, 0), Duration.ofDays(1));
        taskManager.updateSubtask(newSubtask);
        assertEquals(subtask1, taskManager.getSubTask(newSubtask.getId()).orElseThrow(), "Сабтаски конфликтуют");
    }

    @Override
    @Test
    void TaskDoesNotConflict() {
        Task newTask = new Task(task1.getName(), task1.getDescription(), task1.getId(), task1.getStatus(), LocalDateTime.of(2024, 1, 6, 0, 0), Duration.ofDays(1));
        taskManager.updateTask(newTask);
        assertEquals(task1, taskManager.getTask(newTask.getId()).orElseThrow(), "Задачи конфликтуют");
    }

    @Override
    @Test
    void historymanagerSavesPreviousVersionOfTask() {
        Optional<Task> optionalTask = taskManager.getTask(task1.getId());

        Task historyTask = optionalTask.orElseThrow();

        Task newTask = new Task("New name", "New description", task1.getId(), Status.DONE, LocalDateTime.of(2024, 1, 7, 0, 0), Duration.ofDays(1));
        taskManager.updateTask(newTask);

        String name = historyTask.getName();
        String description = historyTask.getDescription();
        Status status = historyTask.getStatus();

        assertNotEquals(name, taskManager.getTask(task1.getId()).orElseThrow().getName());
        assertNotEquals(description, taskManager.getTask(task1.getId()).orElseThrow().getDescription());
        assertNotEquals(status, taskManager.getTask(task1.getId()).orElseThrow().getStatus());
    }

    @Override
    @Test
    void historymanagerSavesPreviousVersionOfSubtask() {
        Subtask historySubtask = taskManager.getSubTask(subtask1.getId()).orElseThrow();

        Subtask newSubtask = new Subtask("New name", "New description", subtask1.getId(), Status.DONE, epic1.getId(), LocalDateTime.of(2024, 1, 8, 0, 0), Duration.ofDays(1));
        taskManager.updateSubtask(newSubtask);

        String name = historySubtask.getName();
        String description = historySubtask.getDescription();
        Status status = historySubtask.getStatus();

        assertNotEquals(name, taskManager.getSubTask(subtask1.getId()).orElseThrow().getName(), "Старая версия имени");
        assertNotEquals(description, taskManager.getSubTask(subtask1.getId()).orElseThrow().getDescription(), "Старая версия описания");
        assertNotEquals(status, taskManager.getSubTask(subtask1.getId()).orElseThrow().getStatus(), "Старая версия статуса");
    }

    @Override
    @Test
    void historymanagerSavesPreviousVersionOfEpic() {

        Epic historyEpic = taskManager.getEpic(epic1.getId()).orElseThrow();

        Epic newEpic = new Epic("New name", "New description", epic1.getId(), taskManager.getEpic(epic1.getId()).orElseThrow().getSubtasksId());
        taskManager.updateEpic(newEpic);

        Subtask newSubtask = new Subtask("Some name", "Some description", epic1.getId(), LocalDateTime.of(2024, 1, 15, 0, 0), Duration.ofDays(1));

        taskManager.addNewSubtask(newSubtask);

        Subtask updatedSubtask = new Subtask("Some name", "Some description", newSubtask.getId(), Status.DONE, epic1.getId(), LocalDateTime.of(2024, 1, 9, 0, 0), Duration.ofDays(1));
        taskManager.updateSubtask(updatedSubtask);

        String name = historyEpic.getName();
        String description = historyEpic.getDescription();
        Status status = historyEpic.getStatus();
        List<Integer> subtasksId = historyEpic.getSubtasksId();

        assertNotEquals(name, taskManager.getEpic(epic1.getId()).orElseThrow().getName(), "Старая версия имени");
        assertNotEquals(description, taskManager.getEpic(epic1.getId()).orElseThrow().getDescription(), "Старая версия описания");
        assertNotEquals(status, taskManager.getEpic(epic1.getId()).orElseThrow().getStatus(), "Старая версия статуса");
        assertFalse(Arrays.equals(subtasksId.toArray(), taskManager.getEpic(epic1.getId()).orElseThrow().getSubtasksId().toArray()), "Старая версия подзадач");
    }

    @Override
    @Test
    void addNewTask() {

        int numberOfTasks = taskManager.getTasks().size();

        Task task = new Task("Test addNewTask", "Test addNewTask description", LocalDateTime.of(2024, 1, 10, 0, 0), Duration.ofDays(1));
        final int taskId = taskManager.addNewTask(task);
        final Task savedTask = taskManager.getTask(taskId).orElseThrow();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(numberOfTasks + 1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getLast(), "Задачи не совпадают.");
    }

    @Override
    @Test
    void addNewEpic() {

        int numberOfEpics = taskManager.getEpics().size();

        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addNewEpic(epic);
        final Epic savedEpic = taskManager.getEpic(epicId).orElseThrow();

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(numberOfEpics + 1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getLast(), "Эпики не совпадают.");
    }

    @Override
    @Test
    void addNewSubtask() {

        int numberOfSubtasks = taskManager.getSubTasks().size();

        Subtask subtask = new Subtask("Test addNewTask", "Test addNewTask description", epic1.getId(), LocalDateTime.of(2024, 1, 11, 0, 0), Duration.ofDays(1));
        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubTask(subtaskId).orElseThrow();

        assertNotNull(savedSubtask, "Сабтаска не найдена.");
        assertEquals(subtask, savedSubtask, "Сабтаски не совпадают.");

        final List<Subtask> subtasks = taskManager.getSubTasks();

        assertNotNull(subtasks, "Сабтаски не возвращаются.");
        assertEquals(numberOfSubtasks + 1, subtasks.size(), "Неверное количество сабтасок.");
        assertEquals(subtask, subtasks.getLast(), "сабтаски не совпадают.");
    }

    @Override
    @Test
    void removeTasks() {
        taskManager.removeTasks();
        assertEquals(0, taskManager.getTasks().size(), "Задачи не удалены");
    }

    @Override
    @Test
    void removeHistoryOfTasksAndEpics() {
        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubTask(subtask1.getId());
        assertEquals(3, taskManager.getHistory().size(), "Задачи не добавлены");
        taskManager.removeTasks();
        assertEquals(2, taskManager.getHistory().size(), "Таски не удалены");
        taskManager.removeEpics();
        assertEquals(0, taskManager.getHistory().size(), "Эпики удалены некорректно");

    }

    @Override
    @Test
    void removeHistoryOfSubtasks() {
        taskManager.getSubTask(subtask1.getId());
        assertEquals(1, taskManager.getHistory().size(), "Подзадачи не добавлены");
        taskManager.removeSubTasks();
        assertEquals(0, taskManager.getHistory().size(), "Подзадачи удалены некорректно");
    }

    @Override
    @Test
    void removeIdOfSubtask() {
        int id = subtask1.getId();
        assertTrue(epic1.getSubtasksId().contains(id), "Id добавлен");
        taskManager.removeSubtask(id);
        assertFalse(epic1.getSubtasksId().contains(id), "Id не удален");
    }

    @Override
    @Test
    void removeEpics() {
        taskManager.removeEpics();
        assertEquals(0, taskManager.getEpics().size(), "Эпики не удалены");
        assertEquals(0, taskManager.getSubTasks().size(), "Сабтаски не удалены");
    }

    @Override
    @Test
    void removeSubtasks() {
        taskManager.removeSubTasks();
        assertEquals(0, taskManager.getSubTasks().size(), "Эпики не удалены");
    }

    @Override
    @Test
    void updateEpicStatus() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test new subtask", "Test new subtask", epicId, LocalDateTime.of(2024, 1, 12, 0, 0), Duration.ofDays(1));

        final int subtaskId = taskManager.addNewSubtask(subtask);
        final Epic savedEpic = taskManager.getEpic(epicId).orElseThrow();

        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус не соответствует ожидаемому");

        subtask = new Subtask("Test new subtask", "Test new subtask", subtaskId, Status.IN_PROGRESS, epicId, LocalDateTime.of(2024, 1, 13, 0, 0), Duration.ofDays(1));

        taskManager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус не соответствует ожидаемому");

        subtask = new Subtask("Test new subtask", "Test new subtask", subtaskId, Status.DONE, epicId, LocalDateTime.of(2024, 1, 14, 0, 0), Duration.ofDays(1));

        taskManager.updateSubtask(subtask);

        assertEquals(Status.DONE, savedEpic.getStatus(), "Статус не соответствует ожидаемому");

        Subtask subtask2 = new Subtask("Test new subtask2", "Test new subtask2", epicId, LocalDateTime.of(2024, 1, 18, 0, 0), Duration.ofDays(1));

        taskManager.addNewSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Статус не соответствует ожидаемому");
    }

    @Override
    @Test
    void getSubtasksOfEpic() {
        Subtask[] subtasks = {subtask1};
        assertArrayEquals(subtasks, taskManager.getSubTasksOfEpic(epic1.getId()).toArray(), "Сабтаски эпика не совпадают");
    }

    @Override
    @Test
    void changeId() {
        task1.setId(task1.getId() + 1);
        assertFalse(taskManager.getTask(task1.getId()).isPresent(), "Изменение id привело к неправильной работе");
    }

    @Override
    @Test
    void checkIntervalsOfCalendar() {

        taskManager.removeEpics();
        taskManager.removeTasks();

        epic1 = new Epic("Test1", "Test1");
        taskManager.addNewEpic(epic1);

        subtask1 = new Subtask("Test2", "Test2", epic1.getId(), LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));
        task1 = new Task("Test3", "Test3", LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));

        taskManager.addNewSubtask(subtask1);
        assertFalse(taskManager.taskIsValid(task1), "Интервалы пересекаются");
    }

    @Override
    @Test
    void priorityCheck() {

        taskManager.removeEpics();
        taskManager.removeTasks();

        epic1 = new Epic("Test1", "Test1");
        taskManager.addNewEpic(epic1);

        subtask1 = new Subtask("Test2", "Test2", epic1.getId(), LocalDateTime.of(2024, 1, 3, 0, 0), Duration.ofDays(1));
        task1 = new Task("Test3", "Test3", LocalDateTime.of(2024, 1, 2, 0, 0), Duration.ofDays(1));
        Subtask subtask2 = new Subtask("Test1", "Test1", epic1.getId(), LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewTask(task1);
        taskManager.addNewSubtask(subtask2);
        List<Task> prioritets = taskManager.getPrioritizedTasks();

        assertEquals(prioritets.get(0), subtask2, "Задача соответствует приоритету");
        assertEquals(prioritets.get(1), task1, "Задача соответствует приоритету");
        assertEquals(prioritets.get(2), subtask1, "Задача соответствует приоритету");
    }

    @Override
    @Test
    void epicDatesCheck() {

        taskManager.removeEpics();
        taskManager.removeTasks();

        epic1 = new Epic("Test1", "Test1");
        taskManager.addNewEpic(epic1);

        subtask1 = new Subtask("Test2", "Test2", epic1.getId(), LocalDateTime.of(2024, 3, 3, 0, 0), Duration.ofDays(1));
        Subtask subtask2 = new Subtask("Test1", "Test1", epic1.getId(), LocalDateTime.of(2024, 1, 1, 0, 0), Duration.ofDays(1));

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(epic1.getStartTime(), subtask2.getStartTime(), "Задача соответствует приоритету");
        assertEquals(epic1.getEndTime(), subtask1.getEndTime(), "Задача соответствует приоритету");
    }
}