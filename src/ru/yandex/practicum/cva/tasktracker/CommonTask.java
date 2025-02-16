package ru.yandex.practicum.cva.tasktracker;

public class CommonTask extends Task{
    public CommonTask(String name) {
        super(name);
        this.taskType = TaskTypes.DEFAULT;
    }

    public CommonTask(String name, String description) {
        super(name, description);
        this.taskType = TaskTypes.DEFAULT;
    }
}
