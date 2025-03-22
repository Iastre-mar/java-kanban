package ru.yandex.practicum.cva.task.tracker;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    List<Task> getHistory();
}
