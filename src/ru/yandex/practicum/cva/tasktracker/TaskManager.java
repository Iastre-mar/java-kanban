package ru.yandex.practicum.cva.tasktracker;

import java.util.*;


/*  Требования к классу:

    Возможность хранить задачи всех типов. Для этого вам нужно выбрать
    подходящую коллекцию.
    Методы для каждого из типа задач (Задача/Эпик/Подзадача):
     a. Получение списка всех задач.
     b. Удаление всех задач.
     c. Получение по идентификатору.
     d. Создание. Сам объект должен передаваться в качестве параметра.
     e. Обновление. Новая версия объекта с верным идентификатором
     передаётся
      в виде параметра.
     f. Удаление по идентификатору.
    Дополнительные методы:
     a. Получение списка всех подзадач определённого эпика.
    Управление статусами осуществляется по следующему правилу:
     a. Менеджер сам не выбирает статус для задачи. Информация о нём
     приходит менеджеру вместе с информацией о самой задаче. По
     этим данным
     в одних случаях он будет сохранять статус, в других будет
     рассчитывать.
     b. Для эпиков:
        если у эпика нет подзадач или все они имеют статус NEW, то
        статус
        должен быть NEW.
        если все подзадачи имеют статус DONE, то и эпик считается
        завершённым — со статусом DONE.
        во всех остальных случаях статус должен быть IN_PROGRESS.

*/

// Это уже выровненный код, за совет спасибо
public class TaskManager {
    private int                    lastID     = 0;
    private Map<Integer, Task>     taskMap    = new HashMap<>();
    private Map<Integer, EpicTask> epicMap    = new HashMap<>();
    private Map<Integer, SubTask>  subTaskMap = new HashMap<>();


    public Task createTask(Task task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            taskMap.put(task.getId(), task);
        }

        return task;

    }

    public EpicTask createEpic(EpicTask task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            epicMap.put(task.getId(), task);
        }

        return task;
    }

    public SubTask createSubTask(SubTask task) {

        if (task.getId() == 0) {
            int currentId = this.generateId();
            task.setId(currentId);
            subTaskMap.put(task.getId(), task);
        }

        return task;
    }

    public List<Task> getAllTask() {
        return taskMap.values().stream().toList();
    }

    public List<EpicTask> getAllEpic() {
        return epicMap.values().stream().toList();
    }

    public List<SubTask> getAllSubtask() {
        return subTaskMap.values().stream().toList();
    }

    public void deleteAllTask() {
        this.taskMap.clear();
    }

    /* Дошло)))
     */
    public void deleteAllEpic() {
        Set<Integer> copyIDSet  = new HashSet<>(epicMap.keySet());
        for (Integer id : copyIDSet) {
            deleteEpicById(id);
        }
    }

    /* Дошло))))
     */
    public void deleteAllSubtask() {
        Set<Integer> copyIDSet  = new HashSet<>(subTaskMap.keySet());
        for (Integer id : copyIDSet) {
            deleteSubtaskById(id);
        }
    }

    public Task getTaskById(int id) {
        return this.taskMap.get(id);
    }

    public EpicTask getEpicById(int id) {
        return this.epicMap.get(id);
    }

    public SubTask getSubtaskById(int id) {
        return this.subTaskMap.get(id);
    }

    public Task updateTask(Task task) {
        Task oldTask = getTaskById(task.getId());
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        return oldTask;
    }

    public SubTask updateSubTask(SubTask task) {

        int     id      = task.getId();
        SubTask oldTask = getSubtaskById(id);
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());

        int oldParentId = oldTask.getParentId();

        if (oldParentId != 0) {
            getEpicById(oldParentId).removeSubtask(id);
            checkStatusOfEpic(oldParentId);
        }

        int newParentId = task.getParentId();
        oldTask.setParentId(newParentId);

        getEpicById(newParentId).addNewSubtask(id);

        oldTask.setStatus(task.getStatus());

        checkStatusOfEpic(newParentId);

        return oldTask;
    }

    public EpicTask updateEpic(EpicTask task) {
        EpicTask oldTask = getEpicById(task.getId());
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setSubtasksId(task.getSubtasksIDs());

        checkStatusOfEpic(task.getId());
        return oldTask;
    }

    public Task deleteTaskById(int id) {
        return this.taskMap.remove(id);
    }

    public EpicTask deleteEpicById(int id) {
        EpicTask epic = this.epicMap.get(id);
        if (epic != null) {
            List<SubTask> subTaskList = getTasksOfEpic(epic.getId());
            for (SubTask subTask : subTaskList) {
                deleteSubtaskById(subTask.getId());
            }
        }

        return this.epicMap.remove(id);
    }

    public SubTask deleteSubtaskById(int id) {
        SubTask subTask = subTaskMap.remove(id);

        if (subTask != null) {
            int parentId = subTask.getParentId();
            getEpicById(parentId).removeSubtask(id);
            checkStatusOfEpic(parentId);
        }

        return subTask;
    }

    private int generateId() {
        return ++this.lastID;
    }

    public List<SubTask> getTasksOfEpic(int id) {

        EpicTask epicTask =
                epicMap.getOrDefault(id, new EpicTask(""));


        Set<Integer> ids = epicTask.getSubtasksIDs();


        return subTaskMap.entrySet()
                         .stream()
                         .filter(entry -> ids.contains(entry.getKey()))
                         .map(Map.Entry::getValue)
                         .toList();
    }

    private void checkStatusOfEpic(int id) {
        EpicTask epic = getEpicById(id);
        if (epic == null) return;

        List<SubTask> subTasksOfEpic = getTasksOfEpic(id);
        Statuses      newStatus;

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


}
