package ru.yandex.javacource.tsvetkov.javacanban.task;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public void removeSubtasks() {
        subtasksId.clear();
    }

    public ArrayList<Integer> getSubtasksId() {
        return new ArrayList<>(subtasksId);
    }

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description, int id, ArrayList<Integer> subtasksId) {
        super(name, description, id, Status.NEW);
        this.subtasksId = subtasksId;
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
}
