package ru.yandex.practicum.cva.task.tracker;

public interface TaskManager {
    Task createTask(Task task);

    Task getTaskById(int id);

    Task updateTask(Task task);

    Task deleteTaskById(int id);
}
