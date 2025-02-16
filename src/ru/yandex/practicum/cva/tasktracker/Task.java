package ru.yandex.practicum.cva.tasktracker;

import java.util.Objects;

/** Абстрактный класс задачи.
 <p>
    Обладает:
 <p>
         id - присваивается при 'создании' в TaskManager,
         а не при реальном создании объекта.
 <p>
        name - Имя задачи.
 <p>
        description - Описание задачи.
 <p>
        status - Статус задачи, одно из значений enum Statuses.
 <p>
        taskType - Тип задачи, одно из значений enum TaskTypes.

 */
public abstract class Task {
    protected int       id;
    protected String    name;
    protected String    description;
    protected Statuses  status;
    protected TaskTypes taskType;

    protected Task(String name, String description) {
        this.name        = name;
        this.description = description;
        this.status = Statuses.NEW;
    }

    protected Task(String name) {
        this.name = name;
        this.status = Statuses.NEW;
    }


    public TaskTypes getTaskType() {
        return this.taskType;
    }

    public Statuses getStatus() {
        return this.status;
    }

    public void setStatus(Statuses status){
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
               ", taskType=" +
               taskType +
               '}';
    }
}
