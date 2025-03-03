package ru.yandex.practicum.cva.task.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    void testEquals() {
        SubTask firstSubTask = new SubTask("Первая подзадача");
        SubTask secondSubTask = new SubTask(
                "Вторая подзадача",
                "Принадлежит первому Эпику"
        );

        firstSubTask.setId(1);
        secondSubTask.setId(2);

        assertNotEquals(firstSubTask, secondSubTask);

        secondSubTask.setId(1);

        assertEquals(firstSubTask, secondSubTask);
    }

    @Test
    void setParentId() {
        SubTask firstSubTask = new SubTask("Первая подзадача");

        firstSubTask.setId(2);
        firstSubTask.setParentId(1);

        assertEquals(1, firstSubTask.getParentId());

        firstSubTask.setParentId(1);

        assertEquals(1, firstSubTask.getParentId());
    }
}