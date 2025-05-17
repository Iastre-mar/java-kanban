package ru.yandex.practicum.cva.task.tracker;

public class NonExistentEntityException extends RuntimeException {
    public NonExistentEntityException(String message) {
        super(message);
    }
}
