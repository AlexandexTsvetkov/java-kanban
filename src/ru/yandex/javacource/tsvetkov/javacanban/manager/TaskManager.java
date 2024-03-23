package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Task;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subTasks;

    public TaskManager() {
        idCounter = 0;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public int generateId() {
        return ++idCounter;
    }

    public int addNewTask(Task task) {
        final int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    public int addNewEpic(Epic epic) {
        final int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    public Integer addNewSubtask(Subtask subTask) {

        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return null;
        }

        final int id = generateId();
        subTask.setId(id);
        epic.addSubtaskId(id);
        subTasks.put(id, subTask);
        updateEpicStatus(epicId);
        return id;
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {

        Epic epicToRemove = epics.remove(id);

        if (epicToRemove == null) {
            return;
        }

        ArrayList<Integer> subtasksId = epicToRemove.getSubtasksId();
        for (int idOfSubtask : subtasksId) {
            subTasks.remove(idOfSubtask);
        }

    }

    public void removeSubtask(int id) {

        Subtask subtask = subTasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);

        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask savedSubtask = subTasks.get(id);

        if (savedSubtask == null) {
            return;
        }
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }
        subTasks.put(id, subtask);
        updateEpicStatus(epicId);
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();

        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubTasksOfEpic(int epicId) {

        ArrayList<Subtask> subTaskOfEpic = new ArrayList<>();

        Epic epic = epics.get(epicId);

        if (epic != null) {

            for (Subtask subtask : subTasks.values()) {

                if (subtask.getEpicId() == epicId) {
                    subTaskOfEpic.add(subtask);
                }
            }
        }
        return subTaskOfEpic;
    }

    public void removeTasks() {
        tasks.clear();
    }

    public void removeSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {

            if (epic != null) {
                epic.removeSubtasks();
                updateEpicStatus(epic.getId());
            }
        }
    }

    public void removeEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubTask(int id) {
        return subTasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    private void updateEpicStatus(int epicId) { //проверить, сужествует ли эпик в месте вызова

        Epic epic = epics.get(epicId);

        ArrayList<Integer> subtasksId = epic.getSubtasksId();

        if (subtasksId.isEmpty()) {
            epic.status = Status.NEW;
        } else {

            Status statusOfEpic = subTasks.get(subtasksId.getFirst()).getStatus();

            for (int subtaskId : subtasksId) {

                if (subTasks.get(subtaskId).getStatus() != statusOfEpic) {
                    epic.status = Status.IN_PROGRESS;
                    return;
                }
            }
            epic.status = statusOfEpic;
        }
    }
}
