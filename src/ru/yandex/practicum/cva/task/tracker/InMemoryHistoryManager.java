package ru.yandex.practicum.cva.task.tracker;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private int capacity;
    private Deque<Task> tasks;

    public InMemoryHistoryManager(int capacity) {
        this.capacity = capacity;
        this.tasks = new ArrayDeque<>(this.capacity);

    }

    public InMemoryHistoryManager() {
        this.capacity = 10;
        this.tasks = new ArrayDeque<>(10);
    }

    @Override
    public List<Task> getHistory() {
        return tasks.stream().toList();
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public <T extends Task> void add(T task) {
        if (task != null) {
            if (this.tasks.size() == this.capacity) {
                tasks.pollFirst();
            }
            this.tasks.offerLast(task.clone());
        }
    }

    /**
     * for test purposes only
     */
    void put(Task... tasks) {
        for (Task task : tasks) {
            add(task);
        }
    }

}
