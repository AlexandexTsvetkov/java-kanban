package ru.yandex.javacource.tsvetkov.javacanban.task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId;

    public void removeSubtasks() {
        subtasksId.clear();
    }

    public List<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, int id, List<Integer> subtasksId) {
        super(name, description, id, Status.NEW);
        this.subtasksId = subtasksId;
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, int id, List<Integer> subtasksId, Status status) {
        super(name, description, id, status);
        this.subtasksId = subtasksId;
        this.taskType = TaskType.EPIC;
    }

    public void addSubtaskId(int subtaskId) {

        boolean isAdded = false;

        for (int id : subtasksId) {

            if (id == subtaskId) {
                isAdded = true;
                break;
            }
        }

        if (!(isAdded)) {
            subtasksId.add(subtaskId);
        }
    }

    public void removeSubtask(int id) {

        for (int i = 0; i < subtasksId.size(); i++) {

            if (id == subtasksId.get(i)) {
                subtasksId.remove(i);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "ru.yandex.javacource.tsvetkov.javacanban.task.Epic{" +
                "subtasksId=" + subtasksId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }
}
