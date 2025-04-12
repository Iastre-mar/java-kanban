package ru.yandex.practicum.cva.task.tracker;


public class SubTask extends Task {
    private int parentId;

    public SubTask(String name, String description) {
        super(name, description);
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(String name) {
        super(name);
        this.taskType = TaskType.SUBTASK;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        if (parentId != this.parentId) {
            this.parentId = parentId;
        }
    }

    @Override
    public String toString() {
        return "SubTask{" +
               "id=" +
               id +
               ", name='" +
               name +
               '\'' +
               ", description='" +
               description +
               '\'' +
               ", status=" +
               status +
               ", parentId=" +
               parentId +
               '}';
    }
}
