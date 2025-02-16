package ru.yandex.practicum.cva.tasktracker;

import java.util.ArrayList;
import java.util.List;

/** Класс Эпика.
 <p>
    В отличие от обычной задачи:
 <p>
     1) Хранит в себе список подзадач напрямую.
 <p>
     2) Создает подзадачи.
 <p>
 */
public class EpicTask extends Task {
    List<SubTask> subtasksList = new ArrayList<>();

    public EpicTask(String name, String description) {
        super(name, description);
        this.taskType = TaskTypes.EPIC;
    }

    public EpicTask(String name) {
        super(name);
        this.taskType = TaskTypes.EPIC;
    }

    public Task createSubtask(String nameSubtask) {
        SubTask subTask = new SubTask(nameSubtask, this.id);
        subtasksList.add(subTask);
        return subTask;
    }

    public Task createSubtask(String nameSubtask, String descriptionSubtask) {
        SubTask subTask =
                new SubTask(nameSubtask, descriptionSubtask, this.id);
        subtasksList.add(subTask);
        return subTask;
    }

    @Override
    protected void setId(int id) {
        super.setId(id);
        for (SubTask subTask : subtasksList) {
            subTask.setParentId(id);
        }
    }

    /**
     @param status Статус не используется в методе из-за специфики самого эпика
     */
    @Override
    public void setStatus(Statuses status) {
        if (this.subtasksList.isEmpty()) {
            this.status = Statuses.NEW;
        } else {
            this.status = checkSubtasksEpicStatus();
        }
    }

    private Statuses checkSubtasksEpicStatus() {
        Statuses res = Statuses.IN_PROGRESS;
        List<Statuses> statusesList =
                this.subtasksList.stream().map(Task::getStatus).toList();

        if (statusesList.stream().allMatch(Statuses.NEW::equals)) {
            res = Statuses.NEW;
        } else if (statusesList.stream().allMatch(Statuses.DONE::equals)) {
            res = Statuses.DONE;
        }

        return res;
    }

    protected List<SubTask> getSubtasksList() {
        return this.subtasksList;
    }
}
