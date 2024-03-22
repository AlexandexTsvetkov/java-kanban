import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public static int idCounter;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subTasks;

    public TaskManager() {
        idCounter = 0;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public static void updateIdCounter() {
        TaskManager.idCounter++;
    }

    public void createTask(Task newTask) {

        if (newTask instanceof Subtask newSubtask) {
            subTasks.put(newTask.getId(), newSubtask);
            Epic epicOfSubtask = epics.get(newSubtask.getEpicId());
            epicOfSubtask.addSubtaskId(newTask.getId());
            Epic.updateStatus(subTasks, epicOfSubtask);
        }else if (newTask instanceof Epic epic) {
            epics.put(newTask.getId(), epic);
            Epic.updateStatus(subTasks, epic);
        }else if (newTask != null) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    public void removeTaskById(int id) {

        if (subTasks.containsKey(id)) {
            Subtask subtaskToRemove = subTasks.get(id);
            int epicId = subtaskToRemove.getEpicId();
            Epic epicOfSubtask = epics.get(epicId);
            epicOfSubtask.removeSubtaskById(id);
            subTasks.remove(id);
            Epic.updateStatus(subTasks, epicOfSubtask);
        }else if (epics.containsKey(id)) {
            Epic epicToRemove = epics.get(id);
            ArrayList<Integer> subtasksId = epicToRemove.getSubtasksIdList();

            for (int idOfSubtask : subtasksId) {
                subTasks.remove(idOfSubtask);
            }
            epics.remove(id);

        }else tasks.remove(id);
    }

    public void updateTask(Task updatedTask) {

        if (updatedTask instanceof Subtask subtask) {
            subTasks.put(updatedTask.getId(), subtask);
            Epic epicOfSubtask = epics.get(subtask.getEpicId());
            Epic.updateStatus(subTasks, epicOfSubtask);
        }else if (updatedTask instanceof Epic epic) {
            epics.put(updatedTask.getId(), epic);
            Epic.updateStatus(subTasks, epic);
        }else if (updatedTask != null) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    public ArrayList<Task> getTaskList () {
        ArrayList<Task> valuesList = new ArrayList<>();

        for (Task task : tasks.values()) {
            valuesList.add(Task.copyOf(task));
        }
        return valuesList;
    }

    public ArrayList<Subtask> getSubTaskList () {
        ArrayList<Subtask> valuesList = new ArrayList<>();

        for (Subtask subtask : subTasks.values()) {
            valuesList.add(Subtask.copyOf(subtask));
        }
        return valuesList;
    }

    public ArrayList<Epic> getEpicList () {
        ArrayList<Epic> valuesList = new ArrayList<>();

        for (Epic epic : epics.values()) {
            valuesList.add(Epic.copyOf(epic));
        }
        return valuesList;
    }

    public void removeTasks() {
        tasks.clear();
    }

    public void removeSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeSubtasks();
            Epic.updateStatus(subTasks, epic);
        }
    }

    public void removeEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Task getTaskById(int id) {

        if (tasks.containsKey(id)) {
            return Task.copyOf(tasks.get(id));
        }
        return  null;
    }

    public Subtask getSubtaskById(int id) {

        if (subTasks.containsKey(id)) {
            return Subtask.copyOf(subTasks.get(id));
        }
        return  null;
    }

    public Epic getEpicById(int id) {

        if (epics.containsKey(id)) {
            return Epic.copyOf(epics.get(id));
        }
        return  null;
    }
}
