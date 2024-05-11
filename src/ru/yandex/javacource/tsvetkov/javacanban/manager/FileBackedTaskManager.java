package ru.yandex.javacource.tsvetkov.javacanban.manager;

import ru.yandex.javacource.tsvetkov.javacanban.task.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File managerSaveFile;

    private FileBackedTaskManager(File managerSaveFile) {
        super();
        this.managerSaveFile = managerSaveFile;
    }

    public void save() {

        Path managerFile = managerSaveFile.toPath();

        try {
            if (!Files.exists(managerFile)) {
                Files.createFile(managerFile);
            }
            List<String> fileLines = new ArrayList<>();
            fileLines.add("id,type,name,status,description,epic");
            for (Task task : tasks.values()) {
                fileLines.add(toString(task));
            }
            for (Task task : epics.values()) {
                fileLines.add(toString(task));
            }
            for (Task task : subTasks.values()) {
                fileLines.add(toString(task));
            }
            Files.writeString(managerFile, String.join(",\n", fileLines));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        if (file.exists()) {
            try {
                String textFile = Files.readString(file.toPath());

                if (!textFile.isEmpty()) {
                    String[] taskLines = textFile.split("\n");

                    if (taskLines.length > 1) {
                        int maxId = 0;
                        for (int i = 1; i < taskLines.length; i++) {
                            String textLine = taskLines[i].trim();

                            if (!textLine.isEmpty()) {
                                Task task = fromString(taskLines[i]);
                                int id = task.getId();
                                maxId = Integer.max(id, maxId);

                                if (task instanceof Subtask) {
                                    fileBackedTaskManager.subTasks.put(id, (Subtask) task);
                                } else if (task instanceof Epic) {
                                    fileBackedTaskManager.epics.put(id, (Epic) task);
                                } else {
                                    fileBackedTaskManager.tasks.put(id, task);
                                }
                            }
                        }
                        fileBackedTaskManager.idCounter = maxId;

                        for (Subtask subtask : fileBackedTaskManager.subTasks.values()) {
                            fileBackedTaskManager.epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
                        }
                    }
                }
            } catch (IOException e) {
                throw new ManagerReadException(e.getMessage());
            }
        }

        return fileBackedTaskManager;
    }

    private static Task fromString(String textTask) {
        String[] taskFields = textTask.split(",");
        TaskType taskType = TaskType.valueOf(taskFields[1].trim().toUpperCase());
        Task task = null;
        int id = Integer.parseInt(taskFields[0].trim());
        String discription = taskFields[4].trim();
        String name = taskFields[2].trim();
        Status status = Status.valueOf(taskFields[3].trim().toUpperCase());

        if (taskType == TaskType.TASK) {
            task = new Task(name, discription, id, status);
        } else if (taskType == TaskType.EPIC) {
            task = new Epic(name, discription, id, new ArrayList<>(), status);
        } else if (taskType == TaskType.SUBTASK) {
            task = new Subtask(name, discription, id, status, Integer.parseInt(taskFields[5].trim()));
        }
        return task;
    }

    private static String toString(Task task) {
        String textTask;
        String id = String.valueOf(task.getId());
        String name = task.getName();
        String status = task.getStatus().toString();
        String description = task.getDescription();

        if (task instanceof Subtask subtask) {
            textTask = String.join(",", new String[]{id, TaskType.SUBTASK.toString(), name, status, description, String.valueOf(subtask.getEpicId())});
        } else if (task instanceof Epic) {
            textTask = String.join(",", new String[]{id, TaskType.EPIC.toString(), name, status, description});
        } else {
            textTask = String.join(",", new String[]{id, TaskType.TASK.toString(), name, status, description});
        }
        return textTask;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer addNewSubtask(Subtask subTask) {
        int id = super.addNewSubtask(subTask);
        save();
        return id;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeSubTasks() {
        super.removeSubTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(final String message) {
            super(message);
        }
    }

    public static class ManagerReadException extends RuntimeException {
        public ManagerReadException(final String message) {
            super(message);
        }
    }

    @Override
    protected HistoryManager generateHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
