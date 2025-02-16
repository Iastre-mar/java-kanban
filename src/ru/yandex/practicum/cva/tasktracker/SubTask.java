package ru.yandex.practicum.cva.tasktracker;

/** Класс подзадачи. <p>
    Я не представляю себе как можно создать подзадачу к несуществующему
     Эпику, поэтому вне пакета создание подзадач происходит только
     через существующий объект Эпик.

 */
public class SubTask extends Task{
    protected int parentId;

    protected SubTask(String name, String description, int parentId) {
        super(name, description);
        this.taskType = TaskTypes.SUBTASK;
        this.parentId = parentId;
    }

    protected SubTask(String name, int parentId) {
        super(name);
        this.taskType = TaskTypes.SUBTASK;
        this.parentId = parentId;
    }

    protected void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }
}
