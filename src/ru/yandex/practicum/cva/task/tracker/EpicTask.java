package ru.yandex.practicum.cva.task.tracker;



import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EpicTask extends Task {
    private Set<Integer> setOfSubtasksID = new HashSet<>();
    private LocalDateTime endTime;

    public EpicTask(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
    }

    public EpicTask(String name) {
        super(name);
        this.taskType = TaskType.EPIC;
    }

    public void addNewSubtask(int id) {
        if (id != this.id) {
            this.setOfSubtasksID.add(id);
        }
    }

    public boolean removeSubtask(int id) {
        return this.setOfSubtasksID.remove(id);
    }

    public Set<Integer> getSubtasksIDs() {
        return this.setOfSubtasksID;
    }

    public void setSubtasksId(Set<Integer> subtasksId) {
        this.setOfSubtasksID = subtasksId;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
               "id=" +
               id +
               ", name='" +
               name +
               '\'' +
               ", description='" +
               description +
               '\'' +
               ", status=" +
               status +
               ", setOfSubtasksID=" +
               setOfSubtasksID +
               '}';
    }

    @Override
    public EpicTask clone() {
        EpicTask newEpic = (EpicTask) super.clone();
        newEpic.setOfSubtasksID = new HashSet<>(this.getSubtasksIDs());
        return newEpic;
    }

    @Override
    public LocalDateTime getEndTime(){
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime){
        this.endTime = endTime;
    }

}
