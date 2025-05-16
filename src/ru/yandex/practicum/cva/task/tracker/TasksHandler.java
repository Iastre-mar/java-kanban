package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleEndpoint(HttpExchange exchange,
                                  Endpoint endpoint,
                                  String[] pathParts
    ) throws IOException {
        switch (endpoint) {
            case GET_ALL -> sendText(exchange, getTasks());
            case GET -> sendText(exchange, getTaskById(getId(pathParts)));
            case POST -> send(exchange, handlePost(exchange),
                              StatusCodes.SUCCESS_POST.code);
            case DELETE_ALL -> handleDeleteAll(exchange);
            case DELETE -> handleDelete(exchange, getId(pathParts));
            default -> throw new NonExistentEntityException(
                    "Endpoint not found: %s %s".formatted(
                            exchange.getRequestMethod(),
                            exchange.getRequestURI()
                                    .getPath()));
        }
    }

    private String handlePost(HttpExchange exchange) throws IOException {
        Task task = parseTaskFromRequest(exchange);
        return task.getId() == 0 ? createTask(task) : updateTask(task);
    }

    private Task parseTaskFromRequest(HttpExchange exchange) throws
            IOException {
        String body = new String(exchange.getRequestBody()
                                         .readAllBytes(),
                                 StandardCharsets.UTF_8);
        return gson.fromJson(body, Task.class);
    }

    private String getTasks() {
        return gson.toJson(taskManager.getAllTask());
    }

    private String getTaskById(int id) {
        return gson.toJson(taskManager.getTaskById(id));
    }

    private String createTask(Task task) {
        Task added = taskManager.createTask(task);
        return gson.toJson(added);
    }

    private String updateTask(Task task) {
        Task updated = taskManager.updateTask(task);
        return gson.toJson(updated);
    }

    private void deleteAllTask() {
        taskManager.deleteAllTask();
    }

    private void deleteTaskById(int id) {
        taskManager.deleteTaskById(id);
    }

    private int getId(String[] pathParts) {
        return Integer.parseInt(pathParts[1]);
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        deleteAllTask();
        sendEmptyResponseWithCode(exchange, StatusCodes.SUCCESS.code);
    }

    private void handleDelete(HttpExchange exchange, int id) throws
            IOException {
        deleteTaskById(id);
        sendEmptyResponseWithCode(exchange, StatusCodes.SUCCESS.code);
    }
}