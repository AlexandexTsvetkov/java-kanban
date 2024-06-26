package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subTasks;
    protected final HistoryManager historyManager;
    protected final Set<Task> prioritizedTasks;
    protected final Map<LocalDateTime, Boolean> taskCalendar;

    public InMemoryTaskManager() {
        idCounter = 0;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.taskCalendar = getTaskCalendar();
        this.prioritizedTasks = new TreeSet<>((o1, o2) -> {
            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    @Override
    public int generateId() {
        return ++idCounter;
    }

    @Override
    public int addNewTask(Task task) {

        final int id = generateId();
        task.setId(id);

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(task.getStartTime()) && task.getStartTime() != null) {

            if (taskIsInvalid(task)) {
                throw new TaskValidationException("В этом периоде уже запланированы задачи");
            }
        }

        if (task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.of(0, 1, 1, 0, 0));
            task.setDuration(Duration.ofMinutes(0));
        }

        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }

        if (task.getTaskType() == null) {
            task.setTaskType(TaskType.TASK);
        }

        tasks.put(id, task);
        prioritizedTasks.add(task);
        addTaskToCalendar(task);

        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = generateId();
        epic.setId(id);

        if (epic.getStartTime() == null) {
            epic.setStartTime(LocalDateTime.of(0, 1, 1, 0, 0));
            epic.setDuration(Duration.ofMinutes(0));
        }

        if (epic.getStatus() == null) {
            epic.setStatus(Status.NEW);
        }

        if (epic.getTaskType() == null) {
            epic.setTaskType(TaskType.EPIC);
        }

        epics.put(id, epic);

        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subTask) {

        int epicId = subTask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic == null) {
            throw new NotFoundExeption("Эпик по id = " + epicId + "не найден");
        }

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(subTask.getStartTime()) && subTask.getStartTime() != null) {

            if (taskIsInvalid(subTask)) {
                throw new TaskValidationException("В этом периоде уже запланированы задачи");
            }
        }

        if (subTask.getStartTime() == null) {
            subTask.setStartTime(LocalDateTime.of(0, 1, 1, 0, 0));
            subTask.setDuration(Duration.ofMinutes(0));
        }

        if (subTask.getStatus() == null) {
            subTask.setStatus(Status.NEW);
        }

        if (subTask.getTaskType() == null) {
            subTask.setTaskType(TaskType.SUBTASK);
        }

        final int id = generateId();
        subTask.setId(id);
        epic.addSubtaskId(id);
        subTasks.put(id, subTask);

        prioritizedTasks.add(subTask);
        addTaskToCalendar(subTask);

        updateEpicCondition(epic);
        return id;
    }

    @Override
    public void removeTask(int id) {

        Task task = tasks.get(id);

        if (task == null) {
            throw new NotFoundExeption("Задача по id = " + id + "не найдена");
        }

        prioritizedTasks.remove(task);

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(task.getStartTime()) && task.getStartTime() != null) {
            removeTaskFromCalendar(task);
        }
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {

        Epic epicToRemove = epics.remove(id);

        if (epicToRemove == null) {
            throw new NotFoundExeption("Эпик по id = " + id + "не найден");
        }

        List<Integer> subtasksId = epicToRemove.getSubtasksId();
        for (int idOfSubtask : subtasksId) {
            Subtask subtask = subTasks.get(idOfSubtask);
            prioritizedTasks.remove(subtask);

            if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(subtask.getStartTime()) && subtask.getStartTime() != null) {
                removeTaskFromCalendar(subtask);
            }

            subTasks.remove(idOfSubtask);
            historyManager.remove(idOfSubtask);
        }

        historyManager.remove(id);
    }

    @Override
    public void removeSubtask(int id) {

        Subtask subtask = subTasks.remove(id);
        if (subtask == null) {
            throw new NotFoundExeption("Подзадача по id = " + id + "не найдена");
        }
        prioritizedTasks.remove(subtask);

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(subtask.getStartTime()) && subtask.getStartTime() != null) {
            removeTaskFromCalendar(subtask);
        }

        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(id);
        updateEpicCondition(epic);

        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);

        if (savedTask == null) {
            throw new NotFoundExeption("Задача с id = " + id + " не найдена");
        }

        boolean calendarCleaned = false;

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(savedTask.getStartTime()) && savedTask.getStartTime() != null) {
            removeTaskFromCalendar(savedTask);
            calendarCleaned = true;
        }

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(task.getStartTime()) && task.getStartTime() != null) {

            if (taskIsInvalid(task)) {

                if (calendarCleaned) {
                    addTaskToCalendar(savedTask);
                }
                throw new TaskValidationException("В этом периоде уже запланированы задачи");
            }
        }

        if (task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.of(0, 1, 1, 0, 0));
            task.setDuration(Duration.ofMinutes(0));
        }

        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }

        if (task.getTaskType() == null) {
            task.setTaskType(TaskType.TASK);
        }

        tasks.put(id, task);
        prioritizedTasks.remove(savedTask);
        prioritizedTasks.add(task);
        addTaskToCalendar(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        int epicId = subtask.getEpicId();
        Subtask savedSubtask = subTasks.get(id);

        if (savedSubtask == null) {
            throw new NotFoundExeption("Подзадача с id = " + id + " не найдена");
        }

        boolean calendarCleaned = false;

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(savedSubtask.getStartTime()) && savedSubtask.getStartTime() != null) {
            removeTaskFromCalendar(savedSubtask);
            calendarCleaned = true;
        }

        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }

        if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(subtask.getStartTime()) && subtask.getStartTime() != null) {

            if (taskIsInvalid(subtask)) {

                if (calendarCleaned) {
                    addTaskToCalendar(savedSubtask);
                }
                throw new TaskValidationException("В этом периоде уже запланированы задачи");
            }
        }

        if (subtask.getStartTime() == null) {
            subtask.setStartTime(LocalDateTime.of(0, 1, 1, 0, 0));
            subtask.setDuration(Duration.ofMinutes(0));
        }

        if (subtask.getStatus() == null) {
            subtask.setStatus(Status.NEW);
        }

        if (subtask.getTaskType() == null) {
            subtask.setTaskType(TaskType.SUBTASK);
        }

        subTasks.put(id, subtask);
        prioritizedTasks.remove(savedSubtask);
        prioritizedTasks.add(subtask);
        addTaskToCalendar(subtask);

        updateEpicCondition(epic);
    }

    @Override
    public void updateEpic(Epic epic) {

        final Epic savedEpic = epics.get(epic.getId());

        if (savedEpic == null) {
            throw new NotFoundExeption("Эпик с id = " + epic.getId() + " не найден");
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

        if (epics.get(epicId) == null) {
            throw new NotFoundExeption("Эпик не найден по id = " + epicId);
        }

        return subTasks.values().stream()
                .filter(element -> element.getEpicId() == epicId)
                .collect(Collectors.toList());
    }

    @Override
    public void removeTasks() {
        List<Task> removedTasks = getTasks();
        tasks.clear();

        for (Task task : removedTasks) {
            prioritizedTasks.remove(task);

            if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(task.getStartTime()) && task.getStartTime() != null) {
                removeTaskFromCalendar(task);
            }
            historyManager.remove(task.getId());
        }
    }

    @Override
    public void removeSubTasks() {
        List<Subtask> removedSubasks = getSubTasks();
        subTasks.clear();

        for (Epic epic : epics.values()) {

            if (epic != null) {
                epic.removeSubtasks();
                updateEpicCondition(epic);
            }
        }

        for (Subtask subtask : removedSubasks) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);

            if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(subtask.getStartTime()) && subtask.getStartTime() != null) {
                removeTaskFromCalendar(subtask);
            }
        }
    }

    @Override
    public void removeEpics() {

        List<Epic> removedEpics = getEpics();
        List<Subtask> removedSubasks = getSubTasks();
        epics.clear();
        subTasks.clear();

        for (Epic epic : removedEpics) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }

        for (Subtask subtask : removedSubasks) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);

            if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(subtask.getStartTime()) && subtask.getStartTime() != null) {
                removeTaskFromCalendar(subtask);
            }
        }
    }

    @Override
    public Task getTask(int id) throws NotFoundExeption {

        final Task task = tasks.get(id);

        if (task == null) {
            throw new NotFoundExeption("Задача по id = " + id + "не найдена");
        }

        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubTask(int id) throws NotFoundExeption {

        final Subtask subtask = subTasks.get(id);

        if (subtask == null) {
            throw new NotFoundExeption("Подзадача по id = " + id + "не найдена");
        }

        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) throws NotFoundExeption {

        final Epic epic = epics.get(id);

        if (epic == null) {
            throw new NotFoundExeption("Эпик по id = " + id + "не найден");
        }

        historyManager.add(epic);
        return epic;
    }

    private void updateEpicCondition(Epic epic) {

        updateEpicStatus(epic);
        updateEpicDates(epic);
    }

    private void updateEpicStatus(Epic epic) {

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

    private void updateEpicDates(Epic epic) {

        List<Integer> subtasksId = epic.getSubtasksId();

        if (subtasksId.isEmpty()) {
            epic.setStartTime(LocalDateTime.of(0, 1, 1, 0, 0));
            epic.setDuration(Duration.ofMinutes(0));
            epic.setEndTime(LocalDateTime.of(0, 1, 1, 0, 0));
        } else {

            LocalDateTime dateStartOfEpic = LocalDateTime.MAX;
            LocalDateTime dateEndtOfEpic = LocalDateTime.of(0, 1, 1, 0, 0);

            for (int subtaskId : subtasksId) {

                LocalDateTime dateStartOfSubtask = subTasks.get(subtaskId).getStartTime();


                if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(dateStartOfSubtask) && dateStartOfEpic.isAfter(dateStartOfSubtask)) {
                    dateStartOfEpic = dateStartOfSubtask;
                }

                LocalDateTime dateEndOfSubtask = subTasks.get(subtaskId).getEndTime();

                if (!LocalDateTime.of(0, 1, 1, 0, 0).isEqual(dateEndOfSubtask) && dateEndtOfEpic.isBefore(dateEndOfSubtask)) {
                    dateEndtOfEpic = dateEndOfSubtask;
                }
            }

            if (LocalDateTime.MAX.isEqual(dateEndtOfEpic)) {
                dateEndtOfEpic = LocalDateTime.of(0, 1, 1, 0, 0);
            }

            epic.setStartTime(dateStartOfEpic);
            epic.setEndTime(dateEndtOfEpic);
            epic.setDuration(Duration.between(dateStartOfEpic, dateEndtOfEpic));
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    protected Map<LocalDateTime, Boolean> getTaskCalendar() {

        int year = LocalDateTime.now().getYear();

        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0).minusMinutes(15);
        LocalDateTime endDate = LocalDateTime.of(year + 1, 1, 1, 0, 0);

        Map<LocalDateTime, Boolean> newTaskCalendar = new HashMap<>();

        while (startDate.isBefore(endDate)) {
            newTaskCalendar.put(startDate, false);
            startDate = startDate.plusMinutes(15);
        }
        return newTaskCalendar;
    }

    private boolean taskIsInvalid(Task task) {

        LocalDateTime startTimeInCalendar = roundToRight(task.getStartTime());
        LocalDateTime resultEndTimeInCalendar = roundToLeft(task.getEndTime());

        LocalDateTime endTimeInCalendar = (resultEndTimeInCalendar.isBefore(startTimeInCalendar))
                ? startTimeInCalendar : resultEndTimeInCalendar;

        return taskCalendar.keySet().stream()
                .filter(element -> (element.isAfter(startTimeInCalendar)
                        || element.isEqual(startTimeInCalendar))
                        && (element.isBefore(endTimeInCalendar)
                        || element.isEqual(endTimeInCalendar)))
                .anyMatch(taskCalendar::get);
    }

    protected LocalDateTime roundToRight(LocalDateTime dateTime) {

        int minutes = dateTime.getMinute();
        int roundedMinutes;

        if (minutes < 15) {
            roundedMinutes = 15;
        } else if (minutes < 30) {
            roundedMinutes = 30;
        } else if (minutes < 45) {
            roundedMinutes = 45;
        } else {
            roundedMinutes = 0;
            dateTime = dateTime.plusHours(1);
        }

        return dateTime.withMinute(roundedMinutes).withSecond(0).withNano(0);
    }

    protected LocalDateTime roundToLeft(LocalDateTime dateTime) {

        int minutes = dateTime.getMinute();
        int roundedMinutes;

        if (minutes < 15) {
            roundedMinutes = 0;
        } else if (minutes < 30) {
            roundedMinutes = 15;
        } else if (minutes < 45) {
            roundedMinutes = 30;
        } else {
            roundedMinutes = 45;
        }

        return dateTime.withMinute(roundedMinutes).withSecond(0).withNano(0);
    }

    protected void removeTaskFromCalendar(Task task) {

        LocalDateTime startTimeInCalendar = roundToRight(task.getStartTime());
        LocalDateTime endTimeInCalendar = roundToLeft(task.getEndTime());

        if (endTimeInCalendar.isBefore(startTimeInCalendar)) {
            endTimeInCalendar = startTimeInCalendar;
        }

        while (startTimeInCalendar.isBefore(endTimeInCalendar) || startTimeInCalendar.isEqual(endTimeInCalendar)) {
            taskCalendar.put(startTimeInCalendar, false);
            startTimeInCalendar = startTimeInCalendar.plusMinutes(15);
        }
    }

    protected void addTaskToCalendar(Task task) {

        LocalDateTime startTimeInCalendar = roundToRight(task.getStartTime());
        LocalDateTime endTimeInCalendar = roundToLeft(task.getEndTime());

        if (endTimeInCalendar.isBefore(startTimeInCalendar)) {
            endTimeInCalendar = startTimeInCalendar;
        }

        while (startTimeInCalendar.isBefore(endTimeInCalendar) || startTimeInCalendar.isEqual(endTimeInCalendar)) {
            taskCalendar.put(startTimeInCalendar, true);
            startTimeInCalendar = startTimeInCalendar.plusMinutes(15);
        }
    }
}