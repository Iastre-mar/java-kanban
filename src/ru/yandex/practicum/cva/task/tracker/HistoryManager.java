package ru.yandex.practicum.cva.task.tracker;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);
}
