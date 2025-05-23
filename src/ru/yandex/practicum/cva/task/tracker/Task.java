package ru.yandex.practicum.cva.task.tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;


public class Task implements Cloneable {
    private final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
    protected int id;
    protected String name;
    protected String description;
    protected Statuses status;
    protected TaskType taskType;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description) {
        this.name = name;
        this.status = Statuses.NEW;
        this.taskType = TaskType.TASK;
        this.description = description;
    }

    public Task(String name) {
        this.name = name;
        this.status = Statuses.NEW;
        this.taskType = TaskType.TASK;
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

    public TaskType getTaskType() {
        return taskType;
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

    public LocalDateTime getEndTime() {
        return (startTime == null) ? null : startTime.plus(getDuration());
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(String startTime) {
        try {
            this.startTime = LocalDateTime.parse(startTime, getDtf());
        } catch (DateTimeParseException ignored) {
            // Значит неправильный формат, скорее всего пришел "null", ожидаемо
        }
    }

    public String getStartTimeFormatted() {
        return (startTime == null) ? null : startTime.format(getDtf());
    }

    public Duration getDuration() {
        return Optional.ofNullable(duration)
                       .orElse(Duration.ZERO);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public DateTimeFormatter getDtf() {
        return this.dtf;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Task task && id == task.id;
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
