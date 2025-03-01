package ru.yandex.practicum.cva.task.tracker;


public class SubTask extends Task{
    private int parentId;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public SubTask(String name) {
        super(name);
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
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
