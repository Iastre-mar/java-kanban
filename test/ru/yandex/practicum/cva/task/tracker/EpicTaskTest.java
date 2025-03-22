package ru.yandex.practicum.cva.task.tracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class EpicTaskTest {

    @Test
    void testClone() {
        EpicTask firstEpicTask = new EpicTask("Первый эпик");
        firstEpicTask.setId(1);

        SubTask firstSubTask = new SubTask("Первая подзадача");
        SubTask secondSubTask = new SubTask("Вторая подзадача",
                                            "Принадлежит первому Эпику");

        firstSubTask.setId(2);
        secondSubTask.setId(3);

        firstEpicTask.addNewSubtask(2);
        firstEpicTask.addNewSubtask(3);

        assertEquals(2, firstEpicTask.getSubtasksIDs()
                                     .size());

        EpicTask firstEpicTaskCopy = firstEpicTask.clone();

        firstEpicTask.removeSubtask(2);
        assertEquals(1, firstEpicTask.getSubtasksIDs()
                                     .size());

        assertEquals(2, firstEpicTaskCopy.getSubtasksIDs()
                                         .size());


    }

    @Test
    void epicEquals() {
        EpicTask firstEpicTask = new EpicTask("Первый эпик");
        EpicTask secondEpicTask = new EpicTask("Второй Эпик",
                                               "Описание второго эпика");

        firstEpicTask.setId(1);
        secondEpicTask.setId(2);
        assertNotEquals(firstEpicTask, secondEpicTask);
        secondEpicTask.setId(1);
        assertEquals(firstEpicTask, secondEpicTask);

    }

    @Test
    void addNewSubtask() {
        EpicTask firstEpicTask = new EpicTask("Первый эпик");
        firstEpicTask.setId(1);

        assertEquals(0, firstEpicTask.getSubtasksIDs()
                                     .size());

        firstEpicTask.addNewSubtask(2);
        firstEpicTask.addNewSubtask(3);

        assertEquals(2, firstEpicTask.getSubtasksIDs()
                                     .size());

        firstEpicTask.addNewSubtask(1);

        assertEquals(2, firstEpicTask.getSubtasksIDs()
                                     .size());
    }
}