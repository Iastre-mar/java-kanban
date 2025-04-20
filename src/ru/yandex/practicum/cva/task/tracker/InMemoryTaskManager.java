package ru.yandex.practicum.cva.task.tracker;

import java.time.Duration;
import java.time.LocalDateTime;
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
        copyIDSet.forEach(this::deleteEpicById);
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
            checkEpic(oldParentId);
        }

        int newParentId = task.getParentId();
        oldTask.setParentId(newParentId);

        getEpicByIdInternal(newParentId).addNewSubtask(id);

        oldTask.setStatus(task.getStatus());

        checkEpic(newParentId);

        return oldTask;
    }

    @Override
    public EpicTask updateEpic(EpicTask task) {
        EpicTask oldTask = getEpicByIdInternal(task.getId());
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setSubtasksId(task.getSubtasksIDs());

        checkEpic(task.getId());
        return oldTask;
    }

    @Override
    public Task deleteTaskById(int id) {
        historyManager.remove(id);
        return this.taskMap.remove(id);
    }

    @Override
    public EpicTask deleteEpicById(int id) {
        EpicTask epic = getEpicByIdInternal(id);
        List<SubTask> subTaskList = getTasksOfEpic(epic.getId());
        subTaskList.stream()
                   .mapToInt(Task::getId)
                   .forEach(this::deleteSubtaskById);
        historyManager.remove(epic.getId());

        return this.epicMap.remove(id);
    }

    @Override
    public SubTask deleteSubtaskById(int id) {
        SubTask subTask = getSubtaskByIdInternal(id);

        historyManager.remove(subTask.getId());
        int parentId = subTask.getParentId();
        getEpicByIdInternal(parentId).removeSubtask(id);
        checkEpic(parentId);

        return subTaskMap.remove(id);
    }

    @Override
    public List<SubTask> getTasksOfEpic(int id) {

        EpicTask epicTask = getEpicByIdInternal(id);


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

    private void checkEpic(int id) {
        checkStatusOfEpic(id);
        checkTimeOfEpic(id);
    }

    private void checkStatusOfEpic(int id) {
        EpicTask epic = getEpicByIdInternal(id);
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

    private void checkTimeOfEpic(int id) {
        EpicTask epic = getEpicByIdInternal(id);

        List<SubTask> subTasksOfEpic = getTasksOfEpic(id);

        LocalDateTime startTimeOfEpic = getMinStartTimeOfSubtasksList(subTasksOfEpic);
        epic.setStartTime(startTimeOfEpic);

        LocalDateTime endTimeOfEpic = getMaxEndTimeOfSubtasksList(subTasksOfEpic);

        epic.setEndTime(endTimeOfEpic);

        Duration durationSum = getSumOfPeriodsOfSubtasksInList(subTasksOfEpic);
        epic.setDuration(durationSum);


    }

    private LocalDateTime getMinStartTimeOfSubtasksList(List<SubTask> subTasksOfEpic) {
        return subTasksOfEpic.stream()
                             .map(SubTask::getStartTime)
                             .filter(Objects::nonNull)
                             .min(Comparator.naturalOrder())
                             .orElse(null);
    }

    private LocalDateTime getMaxEndTimeOfSubtasksList(List<SubTask> subTasksOfEpic){
        return subTasksOfEpic.stream()
                             .map(SubTask::getEndTime)
                             .filter(Objects::nonNull)
                             .max(Comparator.naturalOrder())
                             .orElse(null);
    }

    private Duration getSumOfPeriodsOfSubtasksInList(List<SubTask> subTasksOfEpic){
        return subTasksOfEpic.stream()
                      .map(SubTask::getDuration)
                      .reduce(Duration.ZERO,
                              Duration::plus);
    }

    private Task getTaskByIdInternal(int id) {
        return this.taskMap.getOrDefault(id, new Task(""));
    }

    private EpicTask getEpicByIdInternal(int id) {
        return this.epicMap.getOrDefault(id, new EpicTask(""));
    }

    private SubTask getSubtaskByIdInternal(int id) {
        return this.subTaskMap.getOrDefault(id, new SubTask(""));
    }

}
