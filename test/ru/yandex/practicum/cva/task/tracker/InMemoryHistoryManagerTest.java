package ru.yandex.practicum.cva.task.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager tq;

    @BeforeEach
    void setUp() {
        tq = new InMemoryHistoryManager();

        Task firstCommonTask = new Task("Первая");
        Task secondCommonTask = new Task("Вторая", "Описание второй");
        EpicTask firstEpicTask = new EpicTask("Первый эпик");
        EpicTask secondEpicTask = new EpicTask("Второй Эпик",
                                               "Описание второго эпика");

        SubTask firstSubTask = new SubTask("Первая подзадача");
        SubTask secondSubTask = new SubTask("Вторая подзадача",
                                            "Принадлежит первому Эпику");
        SubTask thirdSubTask = new SubTask("Третья подзадача",
                                           "Принадлежит второму Эпику");

        firstCommonTask.setId(1);
        secondCommonTask.setId(2);
        firstEpicTask.setId(3);
        secondEpicTask.setId(4);
        firstSubTask.setId(5);
        secondSubTask.setId(6);
        thirdSubTask.setId(7);

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
        int initialSize = tq.getHistory()
                            .size(); // 7
        tq.add(new Task("testAdd"));
        int sizeAfter = tq.getHistory()
                          .size(); // 8
        assertEquals(initialSize + 1, sizeAfter);
        assertEquals("testAdd", tq.getHistory()
                                  .get(sizeAfter - 1)
                                  .getName());
    }

    @Test
    void removeShallDeleteExistingTaskAndChangeSize() {
        int oldSize = tq.getHistory()
                        .size();
        Task taskWithId1 = tq.getHistory()
                             .stream()
                             .filter(task -> task.getId() == 1)
                             .findFirst()
                             .orElse(null);
        assertNotNull(taskWithId1);
        tq.remove(1);
        taskWithId1 = tq.getHistory()
                        .stream()
                        .filter(task -> task.getId() == 1)
                        .findFirst()
                        .orElse(null);
        assertNull(taskWithId1);
        assertEquals(Integer.valueOf(oldSize), tq.getHistory()
                                                 .size() + 1);
    }

    @Test
    void removeNotExistingTaskShallDoNothing() {
        int oldSize = tq.getHistory()
                        .size();
        Task taskWithId1 = tq.getHistory()
                             .stream()
                             .filter(task -> task.getId() == 1)
                             .findFirst()
                             .orElse(null);
        assertNotNull(taskWithId1);
        tq.remove(-99);
        tq.remove(0);
        tq.remove(1000);
        taskWithId1 = tq.getHistory()
                        .stream()
                        .filter(task -> task.getId() == 1)
                        .findFirst()
                        .orElse(null);
        assertNotNull(taskWithId1);
        assertEquals(Integer.valueOf(oldSize), tq.getHistory()
                                                 .size());

    }

    @Test
    void emptyHistoryShallHave0Tasks() {
        HistoryManager tq = new InMemoryHistoryManager();
        tq.add(new Task("Test"));
        assertEquals(1, tq.getHistory()
                          .size());
        tq.remove(0);
        assertEquals(0, tq.getHistory()
                          .size());
    }


}
