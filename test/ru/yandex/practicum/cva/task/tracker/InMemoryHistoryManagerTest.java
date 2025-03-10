package ru.yandex.practicum.cva.task.tracker;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager tq;

    @BeforeEach
    void setUp() {
        tq = new InMemoryHistoryManager();

        Task firstCommonTask = new Task("Первая");
        Task secondCommonTask = new Task("Вторая", "Описание второй");
        EpicTask firstEpicTask = new EpicTask("Первый эпик");
        EpicTask secondEpicTask = new EpicTask(
                "Второй Эпик",
                "Описание второго эпика"
        );

        SubTask firstSubTask = new SubTask("Первая подзадача");
        SubTask secondSubTask = new SubTask(
                "Вторая подзадача",
                "Принадлежит первому Эпику"
        );
        SubTask thirdSubTask = new SubTask(
                "Третья подзадача",
                "Принадлежит второму Эпику"
        );

        tq.add(firstCommonTask);
        tq.add(secondCommonTask);
        tq.add(firstEpicTask);
        tq.add(secondEpicTask);
        tq.add(firstSubTask);
        tq.add(secondSubTask);
        tq.add(thirdSubTask);
    }

    @Test
    void getTasksShouldReturnListWithHistory() {
        var list = tq.getHistory();
        assertInstanceOf(List.class, list);
        var task = list.getLast();
        assertInstanceOf(Task.class, task);
    }


    @Test
    void addShouldAddItem() {
        int initialSize = tq.getHistory().size(); // 7
        tq.add(new Task("testAdd"));
        int sizeAfter = tq.getHistory().size(); // 8
        assertEquals(initialSize + 1, sizeAfter);
        assertEquals("testAdd", tq.getHistory().get(sizeAfter - 1).getName());
    }

    @Test
    void addShouldGrowQueueUntilCapacity10() {
        int initialSize = tq.getHistory().size(); // 7
        tq.add(new Task("8th"));
        tq.add(new Task("9th"));
        tq.add(new Task("10th"));
        tq.add(new Task("11th"));
        int sizeAfter = tq.getHistory().size(); // 10
        assertEquals(initialSize + 3, sizeAfter);
    }

    @Test
    void shouldImitateQueue() {
        int initialSize = tq.getHistory().size(); // 7
        tq.add(new Task("8th")); // first elem after put ten times
        tq.add(new Task("9th"));
        tq.add(new Task("10th"));
        tq.add(new Task("11th"));
        tq.add(new Task("12th"));
        tq.add(new Task("13th"));
        tq.add(new Task("14th"));
        tq.add(new Task("15th"));
        tq.add(new Task("16th"));
        tq.add(new Task("17th"));
        int sizeAfter = tq.getHistory().size(); // 10
        assertEquals(initialSize + 3, sizeAfter);
        assertEquals("8th", tq.getHistory().getFirst().getName());
    }


}
