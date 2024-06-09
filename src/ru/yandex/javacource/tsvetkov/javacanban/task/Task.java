package ru.yandex.javacource.tsvetkov.javacanban.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    public Status status;
    protected TaskType taskType;
    protected Duration duration;
    protected LocalDateTime startTime;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {

        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, int id, Status status, LocalDateTime startTime, Duration duration) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ru.yandex.javacource.tsvetkov.javacanban.task.Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
