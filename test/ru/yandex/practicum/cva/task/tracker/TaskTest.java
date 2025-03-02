package ru.yandex.practicum.cva.task.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testEquals() {
        Task firstCommonTask  = new Task("Первая");
        Task secondCommonTask = new Task("Вторая", "Описание второй");
        firstCommonTask.setId(1);
        secondCommonTask.setId(2);

        assertNotEquals(firstCommonTask, secondCommonTask);
        secondCommonTask.setId(1);
        assertEquals(firstCommonTask, secondCommonTask);

    }

    @Test
    void testToString() {
        Task firstCommonTask  = new Task("Первая");
        Task secondCommonTask = new Task("Вторая", "Описание второй");
        secondCommonTask.setId(1);
        secondCommonTask.setStatus(Statuses.DONE);
        secondCommonTask.setDescription("Описание второй Тест");

        assertEquals(
                "Task{id=0, name='Первая', description='null', status=NEW}",
                firstCommonTask.toString()
        );
        assertEquals(
                "Task{id=1, name='Вторая', description='Описание второй " +
                "Тест', status=DONE}",
                secondCommonTask.toString()
        );
    }

    @Test
    void testClone() {
        Task firstCommonTask = new Task("Первая");
        Task copy            = firstCommonTask.clone();
        assertEquals(firstCommonTask, copy);
        assertNotSame(firstCommonTask, copy);
    }
}