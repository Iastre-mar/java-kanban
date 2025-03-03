package ru.yandex.practicum.cva.task.tracker;

public class Managers {
    private static TaskManager defaultTaskManager;
    private static HistoryManager defaultHistoryManager;

    private Managers() {
    }

    public static TaskManager getDefault() {
        if (Managers.defaultTaskManager == null) {
            Managers.defaultTaskManager = new InMemoryTaskManager();
        }
        return Managers.defaultTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (Managers.defaultHistoryManager == null) {
            Managers.defaultHistoryManager = new InMemoryHistoryManager();
        }
        return Managers.defaultHistoryManager;
    }
}
