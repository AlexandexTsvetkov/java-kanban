package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.Status;
import ru.yandex.javacource.tsvetkov.javacanban.task.Task;
import ru.yandex.javacource.tsvetkov.javacanban.task.Epic;
import ru.yandex.javacource.tsvetkov.javacanban.task.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subTasks;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        idCounter = 0;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int generateId() {
        return ++idCounter;
    }

    @Override
    public int addNewTask(Task task) {
        final int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return id;
    }

    @Override
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

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {

        Epic epicToRemove = epics.remove(id);

        if (epicToRemove == null) {
            return;
        }

        List<Integer> subtasksId = epicToRemove.getSubtasksId();
        for (int idOfSubtask : subtasksId) {
            subTasks.remove(idOfSubtask);
        }

    }

    @Override
    public void removeSubtask(int id) {

        Subtask subtask = subTasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);

        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    @Override
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

    @Override
    public void updateEpic(Epic epic) {

        final Epic savedEpic = epics.get(epic.getId());

        if (savedEpic == null) {
            return;
        }
        epic.setSubtasksId(savedEpic.getSubtasksId());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubTasksOfEpic(int epicId) {

        List<Subtask> subTaskOfEpic = new ArrayList<>();

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

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {

            if (epic != null) {
                epic.removeSubtasks();
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public void removeEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTask(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTask(int id) {
        final Subtask subtask = subTasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    private void updateEpicStatus(int epicId) {

        Epic epic = epics.get(epicId);

        List<Integer> subtasksId = epic.getSubtasksId();

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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}