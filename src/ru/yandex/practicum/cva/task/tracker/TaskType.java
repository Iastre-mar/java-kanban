package ru.yandex.practicum.cva.task.tracker;


public enum TaskType {
    EPIC, TASK, SUBTASK;

    public Task createSampleTask(String name, TaskType taskType) {
        return switch (taskType) {
            case EPIC -> new EpicTask(name);
            case TASK -> new Task(name);
            case SUBTASK -> new SubTask(name);
        };
    }


}
