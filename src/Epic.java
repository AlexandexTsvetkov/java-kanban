import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public void removeSubtasks() {
        subtasksId.clear();
    }

    public ArrayList<Integer> getSubtasksIdList() {
        return new ArrayList<>(subtasksId);
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public static void updateStatus(HashMap<Integer, Subtask> subTasksMap, Epic epic) {

        if (epic.subtasksId.isEmpty()) {
            epic.status = Status.NEW;
        } else {

            Status statusOfEpic = subTasksMap.get(epic.subtasksId.getFirst()).getStatus();

            for (int subtaskId : epic.subtasksId) {

                if (subTasksMap.get(subtaskId).getStatus() != statusOfEpic) {
                    epic.status = Status.IN_PROGRESS;
                    return;
                }

            }

            epic.status = statusOfEpic;
        }
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

    public static Epic copyOf(Epic epic) {
        Epic newEpic = new Epic(epic.name, epic.description, epic.status, epic.id);
        newEpic.subtasksId = new ArrayList<>(epic.subtasksId);
        return newEpic;
    }

    public void removeSubtaskById(int id) {
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
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
