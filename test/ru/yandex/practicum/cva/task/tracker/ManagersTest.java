package ru.yandex.practicum.cva.task.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        assertInstanceOf(TaskManager.class, Managers.getDefault());


    }

    @Test
    void getDefaultHistory() {
        assertInstanceOf(HistoryManager.class, Managers.getDefaultHistory());
    }
}