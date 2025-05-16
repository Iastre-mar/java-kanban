package ru.yandex.practicum.cva.task.tracker;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    EpicTask createEpic(EpicTask task);

    SubTask createSubTask(SubTask task);

    List<Task> getAllTask();

    List<EpicTask> getAllEpic();

    List<SubTask> getAllSubtask();

    void deleteAllTask();

    void deleteAllEpic();

    void deleteAllSubtask();

    Task getTaskById(int id);

    EpicTask getEpicById(int id);

    SubTask getSubtaskById(int id);

    Task updateTask(Task task);

    SubTask updateSubTask(SubTask task);

    EpicTask updateEpic(EpicTask task);

    Task deleteTaskById(int id);

    EpicTask deleteEpicById(int id);

    SubTask deleteSubtaskById(int id);

    List<SubTask> getTasksOfEpic(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
