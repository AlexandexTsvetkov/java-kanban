package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;

import java.util.ArrayList;
import java.util.List;

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

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubTasksOfEpic(int epicId);

    void removeTasks();

    void removeSubTasks();

    void removeEpics();

    Task getTask(int id);

    Subtask getSubTask(int id);

    Epic getEpic(int id);

    void updateEpicStatus(int epicId);

    List<Task> getHistory();
}
