package ru.yandex.practicum.cva.task.tracker;

public class NonExistentTaskException extends RuntimeException {
    public NonExistentTaskException(String message) {
        super(message);
    }
}
