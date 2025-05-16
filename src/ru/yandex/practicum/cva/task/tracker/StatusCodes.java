package ru.yandex.practicum.cva.task.tracker;

public enum StatusCodes {
    SUCCESS(200),
    SUCCESS_POST(201),
    NOT_FOUND(404),
    NOT_ACCEPTABLE(406),
    INTERNAL_ERROR(500);
    public final int code;

    StatusCodes(int code) {
        this.code = code;
    }
}
