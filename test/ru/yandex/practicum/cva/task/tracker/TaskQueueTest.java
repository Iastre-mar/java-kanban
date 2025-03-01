package ru.yandex.practicum.cva.task.tracker;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class TaskQueueTest {
    TaskQueue tq;

    @BeforeEach
    void setUp() {
        tq = new TaskQueue(10);

        Task     firstCommonTask  = new Task("Первая");
        Task     secondCommonTask = new Task("Вторая", "Описание второй");
        EpicTask firstEpicTask    = new EpicTask("Первый эпик");
        EpicTask secondEpicTask   = new EpicTask(
                "Второй Эпик",
                "Описание второго эпика"
        );

        SubTask firstSubTask  = new SubTask("Первая подзадача");
        SubTask secondSubTask = new SubTask(
                "Вторая подзадача",
                "Принадлежит первому Эпику"
        );
        SubTask thirdSubTask  = new SubTask(
                "Третья подзадача",
                "Принадлежит второму Эпику"
        );

        tq.put(
                firstCommonTask,
                secondCommonTask,
                firstEpicTask,
                secondEpicTask,
                firstSubTask,
                secondSubTask,
                thirdSubTask
        );
    }

    @Test
    void getTasksShouldReturnListWithTasks() {
        var list = tq.getTasks();
        assertInstanceOf(List.class, list);
        var task = list.get(list.size() - 1);
        assertInstanceOf(Task.class, task);
    }

    @Test
    void getCapacityShouldReturn10() {
        assertEquals(10, tq.getCapacity());
    }

    @Test
    void putShouldAddItem() {
        int initialSize = tq.getTasks().size(); // 7
        tq.put(new Task("testAdd"));
        int sizeAfter = tq.getTasks().size(); // 8
        assertEquals(initialSize + 1, sizeAfter);
        assertEquals("testAdd", tq.getTasks().get(sizeAfter - 1).getName());
    }

    @Test
    void putShouldGrowQueueUntilCapacity10() {
        int initialSize = tq.getTasks().size(); // 7
        tq.put(new Task("8th"));
        tq.put(new Task("9th"));
        tq.put(new Task("10th"));
        tq.put(new Task("11th"));
        int sizeAfter = tq.getTasks().size(); // 10
        assertEquals(initialSize + 3, sizeAfter);
    }

    @Test
    void shouldImitateQueue() {
        int initialSize = tq.getTasks().size(); // 7
        tq.put(new Task("8th")); // first elem after put ten times
        tq.put(new Task("9th"));
        tq.put(new Task("10th"));
        tq.put(new Task("11th"));
        tq.put(new Task("12th"));
        tq.put(new Task("13th"));
        tq.put(new Task("14th"));
        tq.put(new Task("15th"));
        tq.put(new Task("16th"));
        tq.put(new Task("17th"));
        int sizeAfter = tq.getTasks().size(); // 10
        assertEquals(initialSize + 3, sizeAfter);
        assertEquals("8th", tq.getTasks().getFirst().getName());
    }

}