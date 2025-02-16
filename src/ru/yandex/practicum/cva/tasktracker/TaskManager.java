package ru.yandex.practicum.cva.tasktracker;

import java.util.*;


/*  Требования к классу:

    Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    Методы для каждого из типа задач (Задача/Эпик/Подзадача):
     a. Получение списка всех задач.
     b. Удаление всех задач.
     c. Получение по идентификатору.
     d. Создание. Сам объект должен передаваться в качестве параметра.
     e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
     f. Удаление по идентификатору.
    Дополнительные методы:
     a. Получение списка всех подзадач определённого эпика.
    Управление статусами осуществляется по следующему правилу:
     a. Менеджер сам не выбирает статус для задачи. Информация о нём приходит менеджеру вместе с информацией о самой задаче. По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.
     b. Для эпиков:
        если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
        если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
        во всех остальных случаях статус должен быть IN_PROGRESS.

*/

public class TaskManager {
    public static volatile int lastID = 0;
    Map<Integer, Task> taskMap = new HashMap<>();


    public List<Task> getTasks(TaskTypes taskType){
        return taskMap.values().stream().filter(task -> task.getTaskType() == taskType).toList();
    }

    public void purgeAllTasksOfType(TaskTypes taskType){
        for (Integer key : taskMap.keySet()){
            Task value = taskMap.get(key);
            if (value.getTaskType() == taskType){
                taskMap.remove(key);
            }
        }
    }

    public Task getTask(int id){return taskMap.get(id);}


    public Task createTask(Task task){

        if (task.getId() == 0 ) {
            int currentId = TaskManager.lastID++;
            task.setId(currentId);

            taskMap.put(currentId, task);
            if (TaskTypes.EPIC.equals(task.taskType)) {
                List<SubTask> listOfSubtasks = getTasksOfEpic(currentId);
                for (Task subtask : listOfSubtasks) {
                    createTask(subtask);
                }
            }
        }

        return task;

    }


    public Task updateTask(Task task) {
        Task oldTask = getTask(task.getId());
        oldTask.setName(task.getName());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        // В тз написано о том что эпик должен чекаться после каждого обновления подзадачи
        if (oldTask instanceof SubTask){
            SubTask currTask = (SubTask) task;
            Task oldEpicTask = getTask(currTask.getParentId());
            oldEpicTask.setStatus(task.getStatus());
        }
        return oldTask;
    }

    public Task removeTask(int id){
        Task task = this.taskMap.get(id);
        if (task instanceof EpicTask){
            List<SubTask> subTasksList = getTasksOfEpic(id);
                for (SubTask subTask : subTasksList) {
                    this.taskMap.remove(subTask.getId());
            }
        }
        task = this.taskMap.remove(id);

        return task;
    }


    public List<SubTask> getTasksOfEpic(int id){

        EpicTask epicTask = (EpicTask) taskMap.get(id);
        if (epicTask == null) return new ArrayList<>();

        return epicTask.getSubtasksList();
    }


}
