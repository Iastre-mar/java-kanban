package ru.yandex.practicum.cva.task.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest
        extends TaskManagerTest<FileBackedTaskManager> {
    private final String backupFileName = "autosave.csv";
    private final String backupDirectory = "out/";
    FileBackedTaskManager tm;
    Task[] tasks;

    @Override
    @BeforeEach
    void setUp() {
        tm = new FileBackedTaskManager();
        super.tm = tm;

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

        firstCommonTask.setStartTime(LocalDateTime.of(2011, 11, 11, 11, 11));
        firstCommonTask.setDuration(
                Duration.of(1, TimeUnit.HOURS.toChronoUnit()));

        secondCommonTask.setStartTime(LocalDateTime.of(2012, 11, 11, 11, 11));
        secondCommonTask.setDuration(
                Duration.of(1, TimeUnit.HOURS.toChronoUnit()));


        firstSubTask.setStartTime(LocalDateTime.of(2007, 11, 11, 11, 11));
        firstSubTask.setDuration(
                Duration.of(364, TimeUnit.DAYS.toChronoUnit()));

        secondSubTask.setStartTime(LocalDateTime.of(2008, 11, 11, 11, 11));
        secondSubTask.setDuration(
                Duration.of(3, TimeUnit.DAYS.toChronoUnit()));

        thirdSubTask.setStartTime(LocalDateTime.of(2009, 1, 1, 11, 11));
        thirdSubTask.setDuration(
                Duration.of(364, TimeUnit.DAYS.toChronoUnit()));


        tasks = new Task[]{
                firstCommonTask,
                secondCommonTask,
                firstEpicTask,
                secondEpicTask,
                firstSubTask,
                secondSubTask,
                thirdSubTask
        };
        super.tasks = tasks;
    }

    /**
     * Can be used after `create` tests
     */
    void setUpFullStand() {
        tm.createTask(tasks[0]);
        tm.createTask(tasks[1]);
        tm.createEpic((EpicTask) tasks[2]);
        tm.createEpic((EpicTask) tasks[3]);
        tm.createSubTask((SubTask) tasks[4]);
        tm.createSubTask((SubTask) tasks[5]);
        tm.createSubTask((SubTask) tasks[6]);

        // id`s in InmemoryTaskManager started with 1
        ((EpicTask) tasks[2]).addNewSubtask(5);
        ((EpicTask) tasks[2]).addNewSubtask(6);
        ((EpicTask) tasks[3]).addNewSubtask(7);

        ((SubTask) tasks[4]).setParentId(3);
        ((SubTask) tasks[5]).setParentId(3);
        ((SubTask) tasks[6]).setParentId(4);

        tm.updateEpic((EpicTask) tasks[2]);
        tm.updateEpic((EpicTask) tasks[2]);

        tm.updateSubTask(((SubTask) tasks[4]));
        tm.updateSubTask(((SubTask) tasks[5]));
        tm.updateSubTask(((SubTask) tasks[6]));
    }

    @Test
    void checkLoadOfBackedTaskManagerShallHaveSameMaps() {
        setUpFullStand();
        File backup = new File(
                String.valueOf(Paths.get(backupDirectory, backupFileName)));

        TaskManager tm2 = FileBackedTaskManager.loadFromFile(backup);

        assertEquals(tm.getAllTask(), tm2.getAllTask());
        assertEquals(tm.getAllSubtask(), tm2.getAllSubtask());
        assertEquals(tm.getAllEpic(), tm2.getAllEpic());

    }

    @Test
    void checkTaskEveryTypeLoad() {
        setUpFullStand();
        File backup = new File(
                String.valueOf(Paths.get(backupDirectory, backupFileName)));

        FileBackedTaskManager tm2 = FileBackedTaskManager.loadFromFile(backup);

        assertEquals(tm.getAllTask()
                       .stream()
                       .map(Task::getName)
                       .toList(), tm2.getAllTask()
                                     .stream()
                                     .map(Task::getName)
                                     .toList());
        assertEquals(tm.getAllEpic()
                       .stream()
                       .map(Task::getName)
                       .toList(), tm2.getAllEpic()
                                     .stream()
                                     .map(Task::getName)
                                     .toList());
        assertEquals(tm.getAllSubtask()
                       .stream()
                       .map(Task::getName)
                       .toList(), tm2.getAllSubtask()
                                     .stream()
                                     .map(Task::getName)
                                     .toList());
        assertEquals(tm.getPrioritizedTasks(), tm2.getPrioritizedTasks());
    }

    @Test
    void checkCorrectnessOfIntervals() {
        setUpFullStand();
        assertEquals(5, tm.getPrioritizedTasks()
                          .size());
    }

}