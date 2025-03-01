package ru.yandex.practicum.cva.task.tracker;


import java.util.*;

public class TaskQueue {
    private int         capacity;
    private Deque<Task> tasks;

    public TaskQueue(int capacity) {
        this.capacity = capacity;
        this.tasks    = new ArrayDeque<>(this.capacity);

    }

    public List<Task> getTasks() {
        return tasks.stream().toList();
    }

    public int getCapacity() {
        return capacity;
    }

    public void put(Task task) {
        if (this.tasks.size() == this.capacity) {
            tasks.pollFirst();
        }
        this.tasks.offerLast(task);
    }

    public void put(Task... tasks) {
        for (Task task : tasks) {
            put(task);
        }
    }

}
