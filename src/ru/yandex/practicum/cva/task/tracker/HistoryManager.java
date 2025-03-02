package ru.yandex.practicum.cva.task.tracker;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    <T extends Task> void add(T task);
}
