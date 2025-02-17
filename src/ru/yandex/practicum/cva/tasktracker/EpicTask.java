package ru.yandex.practicum.cva.tasktracker;


import java.util.HashSet;
import java.util.Set;

public class EpicTask extends Task {
    private Set<Integer> setOfSubtasksID = new HashSet<>();

    public EpicTask(String name, String description) {
        super(name, description);
    }

    public EpicTask(String name) {
        super(name);
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

    public void addNewSubtask(int id){
        this.setOfSubtasksID.add(id);
    }

    public boolean removeSubtask(int id){
        return this.setOfSubtasksID.remove(id);
    }

    public Set<Integer> getSubtasksIDs() {
        return this.setOfSubtasksID;
    }

    public void setSubtasksId(Set<Integer> subtasksId){
        this.setOfSubtasksID = subtasksId;
    }

}
