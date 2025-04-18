package ru.yandex.practicum.cva.task.tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    TaskManager tm;
    Task[] tasks;
    private final String backupFileName = "autosave.csv";
    private final String backupDirectory = "out/";

    @BeforeEach
    void setUp() {
        tm = new FileBackedTaskManager();

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

        tasks = new Task[]{
                firstCommonTask,
                secondCommonTask,
                firstEpicTask,
                secondEpicTask,
                firstSubTask,
                secondSubTask,
                thirdSubTask
        };
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
    void createTaskShouldSetId() {
        Task commonTask = tasks[0];
        assertEquals(Integer.valueOf(0), Integer.valueOf(commonTask.getId()));
        tm.createTask(commonTask);
        assertNotEquals(Integer.valueOf(0),
                        Integer.valueOf(commonTask.getId()));
    }

    @Test
    void createEpicShouldSetId() {
        EpicTask epicTask = (EpicTask) tasks[2];
        assertEquals(Integer.valueOf(0), Integer.valueOf(epicTask.getId()));
        tm.createEpic(epicTask);
        assertNotEquals(Integer.valueOf(0), Integer.valueOf(epicTask.getId()));
    }

    @Test
    void createSubTaskShouldSetId() {
        SubTask subTask = (SubTask) tasks[6];
        assertEquals(Integer.valueOf(0), Integer.valueOf(subTask.getId()));
        tm.createSubTask(subTask);
        assertNotEquals(Integer.valueOf(0), Integer.valueOf(subTask.getId()));
    }

    @Test
    void createMethodsShouldNotOverrideIdsAndNotAssignDifferentIds() {
        Task commonTask = tasks[0];
        tm.createTask(commonTask);
        Integer commonTaskFirstId = Integer.valueOf(commonTask.getId());
        tm.createTask(commonTask);
        Integer commonTaskSecondId = Integer.valueOf(commonTask.getId());
        assertEquals(commonTaskFirstId, commonTaskSecondId);

        EpicTask epicTask = (EpicTask) tasks[2];
        tm.createEpic(epicTask);
        Integer epicTaskFirstId = Integer.valueOf(epicTask.getId());
        tm.createEpic(epicTask);
        Integer epicTaskSecondId = Integer.valueOf(epicTask.getId());
        assertEquals(epicTaskFirstId, epicTaskSecondId);
        assertNotEquals(epicTaskFirstId, commonTaskFirstId);


        SubTask subTask = (SubTask) tasks[6];
        tm.createSubTask(subTask);
        Integer subTaskFirstId = Integer.valueOf(subTask.getId());
        tm.createSubTask(subTask);
        Integer subTaskSecondId = Integer.valueOf(subTask.getId());
        assertEquals(subTaskFirstId, subTaskSecondId);
        assertNotEquals(subTaskFirstId, epicTaskFirstId);
    }

    @Test
    void createMethodsShouldOnlyAssignIds() {
        Task commonTask = tasks[0].clone();
        tm.createTask(commonTask);
        assertNotEquals(tasks[0].id, commonTask.id);
        assertEquals(tasks[0].name, commonTask.name);
        assertEquals(tasks[0].status, commonTask.status);
        assertEquals(tasks[0].description, commonTask.description);

        EpicTask epicTask = (EpicTask) tasks[2].clone();
        tm.createEpic((epicTask));
        assertNotEquals(tasks[2].id, epicTask.id);
        assertEquals(tasks[2].name, epicTask.name);
        assertEquals(tasks[2].status, epicTask.status);
        assertEquals(tasks[2].description, epicTask.description);
        assertEquals(((EpicTask) tasks[2]).getSubtasksIDs(),
                     epicTask.getSubtasksIDs());

        SubTask subTask = (SubTask) tasks[4].clone();
        tm.createSubTask(subTask);
        assertNotEquals(tasks[4].id, subTask.id);
        assertEquals(tasks[4].name, subTask.name);
        assertEquals(tasks[4].status, subTask.status);
        assertEquals(tasks[4].description, subTask.description);
        assertEquals(((SubTask) tasks[4]).getParentId(),
                     subTask.getParentId());

    }

    @Test
    void getAllTaskShouldReturnAllTasks() {
        setUpFullStand();
        List<Task> list = tm.getAllTask();
        assertEquals(2, list.size());
        assertEquals("Первая", list.get(0)
                                   .getName());
        assertEquals("Вторая", list.get(1)
                                   .getName());
    }

    @Test
    void getAllEpicShouldReturnAllEpics() {
        setUpFullStand();
        List<EpicTask> list = tm.getAllEpic();
        assertEquals(2, list.size());
        assertEquals("Первый эпик", list.get(0)
                                        .getName());
        assertEquals("Второй Эпик", list.get(1)
                                        .getName());
    }

    @Test
    void getAllSubtaskShouldReturnAllSubtasks() {
        setUpFullStand();
        List<SubTask> list = tm.getAllSubtask();
        assertEquals(3, list.size());
        assertEquals("Первая подзадача", list.get(0)
                                             .getName());
        assertEquals("Вторая подзадача", list.get(1)
                                             .getName());
        assertEquals("Третья подзадача", list.get(2)
                                             .getName());
    }

    @Test
    void deleteAllTaskShouldDeleteAllTasks() {
        setUpFullStand();
        List<Task> list = tm.getAllTask();
        assertEquals(2, list.size());
        tm.deleteAllTask();
        list = tm.getAllTask();
        assertEquals(0, list.size());
    }

    @Test
    void deleteAllEpicShouldDeleteAllEpicsAndSubtasks() {
        setUpFullStand();
        List<EpicTask> list = tm.getAllEpic();
        List<SubTask> listSubtask = tm.getAllSubtask();

        assertEquals(2, list.size());
        assertEquals(3, listSubtask.size());
        tm.deleteAllEpic();
        list = tm.getAllEpic();
        listSubtask = tm.getAllSubtask();
        assertEquals(0, list.size());
        assertEquals(0, listSubtask.size());

    }

    @Test
    void deleteAllSubtaskShouldDeleteAllSubtasksOnly() {
        setUpFullStand();
        List<EpicTask> list = tm.getAllEpic();
        List<SubTask> listSubtask = tm.getAllSubtask();

        assertEquals(2, list.size());
        assertEquals(3, listSubtask.size());
        tm.deleteAllSubtask();
        list = tm.getAllEpic();
        listSubtask = tm.getAllSubtask();
        assertEquals(2, list.size());
        assertEquals(0, listSubtask.size());
    }

    @Test
    void getTaskByIdShouldReturnRightTask() {
        setUpFullStand();
        Task commonTask = new Task("Третья");
        tm.createTask(commonTask);
        assertEquals(commonTask, tm.getTaskById(commonTask.getId()));
        assertNotEquals(commonTask, tm.getTaskById(commonTask.getId() - 1));
    }

    @Test
    void getEpicByIdShouldReturnRightEpic() {
        setUpFullStand();
        EpicTask epicTask = new EpicTask("Третий эпик");
        tm.createEpic(epicTask);
        assertEquals(epicTask, tm.getEpicById(epicTask.getId()));
        assertNotEquals(epicTask, tm.getEpicById(epicTask.getId() - 1));
    }

    @Test
    void getSubtaskByIdShouldReturnRightSubtask() {
        setUpFullStand();
        SubTask subTask = new SubTask("Четвертая подзадача");
        tm.createSubTask(subTask);
        assertEquals(subTask, tm.getSubtaskById(subTask.getId()));
        assertNotEquals(subTask, tm.getSubtaskById(subTask.getId() - 1));
    }

    @Test
    void updateTaskShouldUpdateStatusOnlyIfStatusChanged() {
        setUpFullStand();
        Task commonTask = new Task("Третья");
        Statuses originalStatus = commonTask.getStatus(); //Shall be NEW
        tm.createTask(commonTask);
        commonTask.setDescription("Третья задача");
        tm.updateTask(commonTask);
        assertEquals(originalStatus, tm.getTaskById(commonTask.getId())
                                       .getStatus());
        commonTask.setStatus(Statuses.DONE);
        tm.updateTask(commonTask);
        assertEquals(Statuses.DONE, tm.getTaskById(commonTask.getId())
                                      .getStatus());

    }

    @Test
    void updateSubTaskShouldUpdateStatusOnlyIfStatusChanged() {
        setUpFullStand();
        SubTask firstSubtask = tm.getSubtaskById(5);
        assertEquals(Statuses.NEW, firstSubtask.getStatus());
        tm.updateSubTask(firstSubtask);
        assertEquals(Statuses.NEW, tm.getSubtaskById(firstSubtask.getId())
                                     .getStatus());
        firstSubtask.setStatus(Statuses.DONE);
        tm.updateSubTask(firstSubtask);
        assertEquals(Statuses.DONE, tm.getSubtaskById(firstSubtask.getId())
                                      .getStatus());

    }

    @Test
    void updateSubTaskShouldRaiseExceptionOnWrongParentId() {
        setUpFullStand();

        SubTask subTask = tm.getSubtaskById(6);
        subTask.setParentId(1);
        assertThrows(NullPointerException.class,
                     () -> tm.updateSubTask(subTask));

    }

    @Test
    void updateEpicShouldUpdateStatusByProjectConditions() {
        setUpFullStand();

        EpicTask firstEpic = tm.getEpicById(3);
        EpicTask secondEpic = tm.getEpicById(4);

        SubTask firstSubtask = tm.getSubtaskById(5);
        SubTask secondSubtask = tm.getSubtaskById(6);
        SubTask thirdSubtask = tm.getSubtaskById(7);


        assertEquals(Statuses.NEW, firstEpic.getStatus());
        assertEquals(Statuses.NEW, secondEpic.getStatus());

        firstSubtask.setStatus(Statuses.DONE);
        tm.updateSubTask(firstSubtask);

        assertEquals(Statuses.IN_PROGRESS, firstEpic.getStatus());
        assertEquals(Statuses.NEW, secondEpic.getStatus());

        thirdSubtask.setStatus(Statuses.DONE);
        tm.updateSubTask(thirdSubtask);
        assertEquals(Statuses.IN_PROGRESS, firstEpic.getStatus());
        assertEquals(Statuses.DONE, secondEpic.getStatus());

        thirdSubtask.setStatus(Statuses.NEW);
        tm.updateSubTask(thirdSubtask);


        assertEquals(Statuses.IN_PROGRESS, firstEpic.getStatus());
        assertEquals(Statuses.NEW, secondEpic.getStatus());

    }

    @Test
    void deleteTaskById() {
        setUpFullStand();

        Task firstCommonTask = tm.deleteTaskById(1);

        assertEquals(Integer.valueOf(1), firstCommonTask.getId());

        Task firstCommonTaskAfterDelete = tm.getTaskById(1);

        assertNull(firstCommonTaskAfterDelete);
    }

    @Test
    void getTasksOfEpicShallReturnSubtasksOfCorrectEpic() {

        setUpFullStand();

        List<SubTask> ListSubtasksFirst = tm.getTasksOfEpic(3);
        List<SubTask> ListSubtasksSecond = tm.getTasksOfEpic(4);

        for (SubTask st : ListSubtasksFirst) {
            assertEquals(3, st.getParentId());
        }

        for (SubTask st : ListSubtasksSecond) {
            assertEquals(4, st.getParentId());
        }


    }

    @Test
    void getHistoryShouldReturnEmptyList() {
        setUpFullStand();

        assertTrue(tm.getHistory()
                     .isEmpty());
    }

    @Test
    void getHistoryShouldReturn1CommonTaskAnd1EpicTaskAnd1Subtask() {
        setUpFullStand();

        tm.getTaskById(1);
        tm.getEpicById(3);
        tm.getSubtaskById(6);
        //tm.getHistory().forEach(System.out::println);
        List<Task> tasks = tm.getHistory();
        assertEquals(3, tasks.size());
        assertInstanceOf(Task.class, tasks.get(0));
        assertInstanceOf(EpicTask.class, tasks.get(1));
        assertInstanceOf(SubTask.class, tasks.get(2));

    }

    @Test
    void getHistoryShouldSaveOldVersionsOfTasks() {
        setUpFullStand();

        Task task = tm.getTaskById(1);
        task.setStatus(Statuses.DONE);
        task.setDescription("Test");
        tm.updateTask(task);
        tm.getTaskById(1);
        //tm.getHistory().forEach(System.out::println);

        assertEquals(task, tm.getHistory()
                             .get(0));
        assertNotEquals(task, tm.getHistory()
                                .get(0)
                                .getStatus());


    }

    @Test
    void getHistoryShouldCorrectlyShowSubtasksInEpics() {
        setUpFullStand();

        EpicTask epicTask = tm.getEpicById(3);
        tm.deleteSubtaskById(epicTask.getSubtasksIDs()
                                     .stream()
                                     .findFirst()
                                     .orElse(0));

        tm.updateEpic(epicTask);
        //tm.getEpicById(3);
        //tm.getHistory().forEach(System.out::println);
        assertEquals(epicTask, tm.getHistory()
                                 .get(0));
        assertNotEquals(epicTask.getSubtasksIDs(), ((EpicTask) tm.getHistory()
                                                                 .get(0)
        ).getSubtasksIDs());


    }

    @Test
    void getHistoryShouldCorrectlyShowOldSubtaskParentId() {
        setUpFullStand();

        SubTask subTask = tm.getSubtaskById(6);
        subTask.setParentId(4);
        //SubTask subtaskNew = tm.getSubtaskById(6);
        //tm.getHistory().forEach(System.out::println);
        assertEquals(subTask, tm.getHistory()
                                .get(0));
        assertNotEquals(subTask.getParentId(), ((SubTask) tm.getHistory()
                                                            .get(0)
        ).getParentId());
    }

    @Test
    void deleteEpicShallRemoveTheirSubtasksFromHistoryToo() {
        setUpFullStand();
        assertEquals(0, tm.getHistory()
                          .size());

        tm.getEpicById(3);
        assertEquals(1, tm.getHistory()
                          .size());
        tm.getEpicById(4);
        assertEquals(2, tm.getHistory()
                          .size());
        tm.getSubtaskById(5);
        tm.getSubtaskById(6);
        assertEquals(4, tm.getHistory()
                          .size());
        tm.deleteEpicById(3);
        assertEquals(1, tm.getHistory()
                          .size());
    }


    @Test
    void getShallAddHistoryOrdered() {
        setUpFullStand();
        assertEquals(0, tm.getHistory()
                          .size());
        tm.getTaskById(2);
        tm.getEpicById(4);
        tm.getTaskById(1);
        assertEquals(2, tm.getHistory()
                          .get(0)
                          .getId());
        assertEquals(4, tm.getHistory()
                          .get(1)
                          .getId());


        tm.getSubtaskById(5);
        tm.deleteTaskById(1);
        assertEquals(5, tm.getHistory()
                          .get(2)
                          .getId());

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
    void checkTaskEveryTypeLoad(){
        setUpFullStand();
        File backup = new File(
                String.valueOf(Paths.get(backupDirectory, backupFileName)));

        TaskManager tm2 = FileBackedTaskManager.loadFromFile(backup);

        assertEquals(tm.getAllTask().stream().map(Task::getName).toList(), tm2.getAllTask().stream().map(Task::getName).toList());
        assertEquals(tm.getAllEpic().stream().map(Task::getName).toList(), tm2.getAllEpic().stream().map(Task::getName).toList());
        assertEquals(tm.getAllSubtask().stream().map(Task::getName).toList(), tm2.getAllSubtask().stream().map(Task::getName).toList());
    }
}