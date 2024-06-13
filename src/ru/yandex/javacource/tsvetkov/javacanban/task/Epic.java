package ru.yandex.javacource.tsvetkov.javacanban.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksId;
    private LocalDateTime endTime;

    public void removeSubtasks() {
        subtasksId.clear();
    }

    public List<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public Epic(String name, String description) {
        super(name, description, LocalDateTime.of(0, 1, 1, 0, 0), Duration.ofMinutes(0));
        this.subtasksId = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.endTime = LocalDateTime.of(0, 1, 1, 0, 0);
    }

    public Epic(String name, String description, int id, List<Integer> subtasksId) {
        super(name, description, id, Status.NEW, LocalDateTime.of(0, 1, 1, 0, 0), Duration.ofMinutes(0));
        this.subtasksId = subtasksId;
        this.taskType = TaskType.EPIC;
        this.endTime = LocalDateTime.of(0, 1, 1, 0, 0);
    }

    public Epic(String name, String description, int id, List<Integer> subtasksId, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.subtasksId = subtasksId;
        this.taskType = TaskType.EPIC;
        this.endTime = startTime.plusMinutes(duration.toMinutes());
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
        return "Epic{" +
                "subtasksId=" + subtasksId +
                ", endTime=" + endTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", taskType=" + taskType +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    public void setSubtasksId(List<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
