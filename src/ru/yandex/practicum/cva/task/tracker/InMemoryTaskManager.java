package ru.yandex.practicum.cva.task.tracker;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, EpicTask> epicMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    protected final HistoryManager historyManager;
    protected int lastID = 0;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public Task createTask(Task task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            taskMap.put(task.getId(), task);
        }

        return task;

    }

    @Override
    public EpicTask createEpic(EpicTask task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            epicMap.put(task.getId(), task);
        }

        return task;
    }

    @Override
    public SubTask createSubTask(SubTask task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            subTaskMap.put(task.getId(), task);
        }

        return task;
    }

    @Override
    public List<Task> getAllTask() {
        return taskMap.values()
                      .stream()
                      .toList();
    }

    @Override
    public List<EpicTask> getAllEpic() {
        return epicMap.values()
                      .stream()
                      .toList();
    }

    @Override
    public List<SubTask> getAllSubtask() {
        return subTaskMap.values()
                         .stream()
                         .toList();
    }

    @Override
    public void deleteAllTask() {
        getAllTask().stream()
                    .map(Task::getId)
                    .forEach(historyManager::remove);
        this.taskMap.clear();
    }

    @Override
    public void deleteAllEpic() {
        Set<Integer> copyIDSet = new HashSet<>(epicMap.keySet());
        for (Integer id : copyIDSet) {
            deleteEpicById(id);
        }
    }

    @Override
    public void deleteAllSubtask() {
        Set<Integer> copyIDSet = new HashSet<>(subTaskMap.keySet());
        for (Integer id : copyIDSet) {
            deleteSubtaskById(id);
        }
    }


    @Override
    public Task getTaskById(int id) {
        Task task = this.taskMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epicTask = this.epicMap.get(id);
        historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubtaskById(int id) {
        SubTask subTask = this.subTaskMap.get(id);
        historyManager.add(subTask);
        return subTask;
    }


    @Override
    public Task updateTask(Task task) {
        Task oldTask = getTaskByIdInternal(task.getId());
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        return oldTask;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {

        int id = task.getId();
        SubTask oldTask = getSubtaskByIdInternal(id);
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());

        int oldParentId = oldTask.getParentId();

        if (oldParentId != 0) {
            getEpicByIdInternal(oldParentId).removeSubtask(id);
            checkStatusOfEpic(oldParentId);
        }

        int newParentId = task.getParentId();
        oldTask.setParentId(newParentId);

        getEpicByIdInternal(newParentId).addNewSubtask(id);

        oldTask.setStatus(task.getStatus());

        checkStatusOfEpic(newParentId);

        return oldTask;
    }

    @Override
    public EpicTask updateEpic(EpicTask task) {
        EpicTask oldTask = getEpicByIdInternal(task.getId());
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setSubtasksId(task.getSubtasksIDs());

        checkStatusOfEpic(task.getId());
        return oldTask;
    }

    @Override
    public Task deleteTaskById(int id) {
        historyManager.remove(id);
        return this.taskMap.remove(id);
    }

    @Override
    public EpicTask deleteEpicById(int id) {
        EpicTask epic = this.epicMap.get(id);
        if (epic != null) {
            List<SubTask> subTaskList = getTasksOfEpic(epic.getId());
            for (SubTask subTask : subTaskList) {
                deleteSubtaskById(subTask.getId());
            }
            historyManager.remove(epic.getId());
        }

        return this.epicMap.remove(id);
    }

    @Override
    public SubTask deleteSubtaskById(int id) {
        SubTask subTask = subTaskMap.remove(id);

        if (subTask != null) {
            historyManager.remove(subTask.getId());
            int parentId = subTask.getParentId();
            getEpicByIdInternal(parentId).removeSubtask(id);
            checkStatusOfEpic(parentId);
        }

        return subTask;
    }

    @Override
    public List<SubTask> getTasksOfEpic(int id) {

        EpicTask epicTask = epicMap.getOrDefault(id, new EpicTask(""));


        Set<Integer> ids = epicTask.getSubtasksIDs();


        return subTaskMap.entrySet()
                         .stream()
                         .filter(entry -> ids.contains(entry.getKey()))
                         .map(Map.Entry::getValue)
                         .toList();
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }

    private int generateId() {
        return ++this.lastID;
    }

    private void checkStatusOfEpic(int id) {
        EpicTask epic = getEpicByIdInternal(id);
        if (epic == null)
            return;

        List<SubTask> subTasksOfEpic = getTasksOfEpic(id);
        Statuses newStatus;

        List<Statuses> distinctStatuses = subTasksOfEpic.stream()
                                                        .map(SubTask::getStatus)
                                                        .distinct()
                                                        .toList();

        if (distinctStatuses.size() > 1) {
            newStatus = Statuses.IN_PROGRESS;
        } else {
            newStatus = distinctStatuses.stream()
                                        .findFirst()
                                        .orElse(Statuses.NEW);
        }
        epic.setStatus(newStatus);

    }

    private Task getTaskByIdInternal(int id) {
        return this.taskMap.get(id);
    }

    private EpicTask getEpicByIdInternal(int id) {
        return this.epicMap.get(id);
    }

    private SubTask getSubtaskByIdInternal(int id) {
        return this.subTaskMap.get(id);
    }

}
