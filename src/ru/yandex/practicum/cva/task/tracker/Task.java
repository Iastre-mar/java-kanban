package ru.yandex.practicum.cva.task.tracker;

import java.util.Objects;


public class Task implements Cloneable {
    protected int      id;
    protected String   name;
    protected String   description;
    protected Statuses status;

    public Task(String name, String description) {
        this.name        = name;
        this.description = description;
        this.status      = Statuses.NEW;
    }

    public Task(String name) {
        this.name   = name;
        this.status = Statuses.NEW;
    }

    public Statuses getStatus() {
        return this.status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
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
               '}';
    }

    @Override
    public Task clone() {
        try {
            Task clone = (Task) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
