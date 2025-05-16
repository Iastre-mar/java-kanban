package ru.yandex.practicum.cva.task.tracker;

public class TaskOverlapException extends RuntimeException {
    public TaskOverlapException(String message) {
        super(message);
    }
}
