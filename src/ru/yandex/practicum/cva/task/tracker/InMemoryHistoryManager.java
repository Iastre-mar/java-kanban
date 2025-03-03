package ru.yandex.practicum.cva.task.tracker;


import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final int capacity;
    private Deque<Task> tasks;


    public InMemoryHistoryManager() {
        this.capacity = 10;
        this.tasks = new ArrayDeque<>(this.capacity);
    }

    @Override
    public List<Task> getHistory() {
        return tasks.stream().toList();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (this.tasks.size() == this.capacity) {
                tasks.pollFirst();
            }
            this.tasks.offerLast(task.clone());
        }
    }

}
