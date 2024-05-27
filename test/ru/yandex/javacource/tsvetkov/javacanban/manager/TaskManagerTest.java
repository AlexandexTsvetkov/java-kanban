package ru.yandex.javacource.tsvetkov.javacanban.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

public abstract class TaskManagerTest<T extends TaskManager> {
    static TaskManager taskManager;
    static Epic epic1;
    static Subtask subtask1;
    static Task task1;

    @Test
    abstract void epicCantBeSubtask();

    @Test
    abstract void subtaskCantBeEpic();

    @Test
    abstract void taskFieldsAreUnchanged();

    @Test
    abstract void subtaskFieldsAreUnchanged();

    @Test
    abstract void epicFieldsAreUnchanged();

    @Test
    abstract void EpicDoesNotConflict();

    @Test
    abstract void SubtaskDoesNotConflict();

    @Test
    abstract void TaskDoesNotConflict();

    @Test
    abstract void historymanagerSavesPreviousVersionOfTask();

    @Test
    abstract void historymanagerSavesPreviousVersionOfSubtask();

    @Test
    abstract void historymanagerSavesPreviousVersionOfEpic();

    @Test
    abstract void addNewTask();

    @Test
    abstract void addNewEpic();

    @Test
    abstract void addNewSubtask();

    @Test
    abstract void removeTasks();

    @Test
    abstract void removeHistoryOfTasksAndEpics();

    @Test
    abstract void removeHistoryOfSubtasks();

    @Test
    abstract void removeIdOfSubtask();

    @Test
    abstract void removeEpics();

    @Test
    abstract void removeSubtasks();

    @Test
    abstract void updateEpicStatus();

    @Test
    abstract void getSubtasksOfEpic();

    @Test
    abstract void changeId();

    @Test
    abstract void epicIsExists();

    @Test
    abstract void checkIntervalsOfCalendar();

    @Test
    abstract void priorityCheck();

    @Test
    abstract void epicDatesCheck();
}
