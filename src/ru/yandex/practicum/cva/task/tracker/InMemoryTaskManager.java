package ru.yandex.practicum.cva.task.tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;


public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> taskMap = new HashMap<>();
    protected final Map<Integer, EpicTask> epicMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    protected final TreeSet<Task> prioritizeTasks;
    protected final HistoryManager historyManager;
    protected int lastID = 0;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
        prioritizeTasks = new TreeSet<>(
                Comparator.comparing(Task::getStartTime,
                                     Comparator.naturalOrder()));
    }

    @Override
    public Task createTask(Task task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            taskMap.put(task.getId(), task);
            addTaskToPrioritizeTasks(task);
        }

        return task;

    }

    @Override
    public EpicTask createEpic(EpicTask task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            epicMap.put(task.getId(), task);
            checkEpic(task.getId());
        }

        return task;
    }

    @Override
    public SubTask createSubTask(SubTask task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            subTaskMap.put(task.getId(), task);
            addTaskToPrioritizeTasks(task);
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
        checkIsEntityExists(task, id, TaskType.TASK);
        historyManager.add(task);
        return task;
    }

    @Override
    public EpicTask getEpicById(int id) {
        EpicTask epicTask = this.epicMap.get(id);
        checkIsEntityExists(epicTask, id, TaskType.EPIC);
        historyManager.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getSubtaskById(int id) {
        SubTask subTask = this.subTaskMap.get(id);
        checkIsEntityExists(subTask, id, TaskType.SUBTASK);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task oldTask = getTaskByIdInternal(task.getId());
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        oldTask.setStartTime(task.getStartTime());
        oldTask.setDuration(task.getDuration());

        addTaskToPrioritizeTasks(task);
        return oldTask;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {

        int id = task.getId();
        SubTask oldTask = getSubtaskByIdInternal(id);
        // Должно сдохнуть не обновив важные данные если epic неправильный
        int newParentId = task.getParentId();
        EpicTask newEpic = getEpicByIdInternal(newParentId);


        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());

        int oldParentId = oldTask.getParentId();


        if (oldParentId != 0) {
            getEpicByIdInternal(oldParentId).removeSubtask(id);
            checkEpic(oldParentId);
        }
        newEpic.addNewSubtask(id);



        oldTask.setParentId(newParentId);

        oldTask.setStatus(task.getStatus());

        checkEpic(newParentId);

        oldTask.setStartTime(task.getStartTime());
        oldTask.setDuration(task.getDuration());

        addTaskToPrioritizeTasks(task);

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
        removeTaskFromPrioritizeTasks(getTaskByIdInternal(id));
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
        removeTaskFromPrioritizeTasks(getSubtaskByIdInternal(id));

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

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizeTasks.stream()
                              .filter(task -> task.getStartTime() != null)
                              .toList();
    }

    protected void addTaskToPrioritizeTasks(Task task) {

        if (checkCanTaskBeAddedToPriorityList(task)) {
            removeTaskFromPrioritizeTasks(task);
            prioritizeTasks.add(task);
        } else {
            throw new TaskOverlapException("task overlapped with other");
        }
    }

    protected void checkEpic(int id) {
        checkStatusOfEpic(id);
        checkTimeOfEpic(id);
    }

    private int generateId() {
        return ++this.lastID;
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

        LocalDateTime startTimeOfEpic = getMinStartTimeOfSubtasksList(
                subTasksOfEpic);
        epic.setStartTime(startTimeOfEpic);

        LocalDateTime endTimeOfEpic = getMaxEndTimeOfSubtasksList(
                subTasksOfEpic);

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

    private LocalDateTime getMaxEndTimeOfSubtasksList(List<SubTask> subTasksOfEpic) {
        return subTasksOfEpic.stream()
                             .map(SubTask::getEndTime)
                             .filter(Objects::nonNull)
                             .max(Comparator.naturalOrder())
                             .orElse(null);
    }

    private Duration getSumOfPeriodsOfSubtasksInList(List<SubTask> subTasksOfEpic) {
        return subTasksOfEpic.stream()
                             .map(SubTask::getDuration)
                             .reduce(Duration.ZERO, Duration::plus);
    }

    private Task getTaskByIdInternal(int id) {
        Task task = this.taskMap.getOrDefault(id, null);
        checkIsEntityExists(task, id, TaskType.TASK);
        return task;
    }

    private EpicTask getEpicByIdInternal(int id) {
        EpicTask epicTask = this.epicMap.getOrDefault(id, null);
        checkIsEntityExists(epicTask, id, TaskType.EPIC);
        return epicTask;
    }

    private SubTask getSubtaskByIdInternal(int id) {
        SubTask subTask = this.subTaskMap.getOrDefault(id, null);
        checkIsEntityExists(subTask, id, TaskType.SUBTASK);
        return subTask;
    }

    private boolean checkCanTaskBeAddedToPriorityList(Task task) {
        boolean res = false;
        try {
            throwNullIfTimeFieldIsNull(task);
            List<Task> prioritizeTasks = getPrioritizedTasks();
            res = prioritizeTasks.stream()
                                 .filter(taskInPriorList -> isIntersectionOfTime(
                                         task, taskInPriorList))
                                 .toList()
                                 .isEmpty();
        } catch (NullPointerException npe) {
            // Ожидаемое поведение
        }
        return res;

    }

    private boolean isIntersectionOfTime(Task task1, Task task2) {
        return (!task1.equals(task2)) && task1.getStartTime()
                    .isBefore(task2.getEndTime()) &&
               task2.getStartTime()
                    .isBefore(task1.getEndTime());
    }

    private void throwNullIfTimeFieldIsNull(Task task) throws
            NullPointerException {
        Stream.of(task.getStartTime(), task.getDuration(), task.getEndTime())
              .map(Objects::requireNonNull)
              .findFirst();

    }

    private void removeTaskFromPrioritizeTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizeTasks.remove(task);
        }
    }

    private void checkIsEntityExists(Task task, int id, TaskType taskType) {
        if (task == null) {
            throw new NonExistentTaskException(
                    "%s with id %s does not exists".formatted(taskType, id));
        }
    }


}
