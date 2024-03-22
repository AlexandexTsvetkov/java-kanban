
public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;

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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Task(String name, String description) {

        TaskManager.updateIdCounter();
        this.id = TaskManager.idCounter;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    protected Task(String name, String description, Status status, int id) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public static Task copyOf(Task task) {
        return new Task(task.name, task.description, task.status, task.id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
