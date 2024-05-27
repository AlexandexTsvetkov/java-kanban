package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
    int generateId();

    int addNewTask(Task task);

    int addNewEpic(Epic epic);

    Integer addNewSubtask(Subtask subTask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    List<Task> getTasks();

    List<Subtask> getSubTasks();

    List<Epic> getEpics();

    List<Subtask> getSubTasksOfEpic(int epicId);

    void removeTasks();

    void removeSubTasks();

    void removeEpics();

    Optional<Task> getTask(int id);

    Optional<Subtask> getSubTask(int id);

    Optional<Epic> getEpic(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean taskIsValid(Task task);
}
