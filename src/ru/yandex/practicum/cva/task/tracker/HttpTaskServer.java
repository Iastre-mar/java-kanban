package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);
        this.taskManager = taskManager;
        this.gson = createGson();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(
                Managers.getDefault());
        httpTaskServer.start();
    }

    private static Gson createGson() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                                     new LocalDateTimeAdapter())
                                .registerTypeAdapter(Duration.class,
                                                     new DurationAdapter())
                                .registerTypeAdapter(EpicTask.class,
                                                     new EpicDeserializer())
                                .registerTypeAdapter(Task.class,
                                                     new TaskDeserializer())
                                .registerTypeAdapter(SubTask.class,
                                                     new SubTaskDeserializer())
                                .create();
    }

    public void start() {
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/subtasks",
                             new SubtasksHandler(taskManager, gson));
        server.createContext("/history",
                             new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized",
                             new PrioritizedTasksHandler(taskManager, gson));

        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public Gson getGson() {
        return gson;
    }
}
