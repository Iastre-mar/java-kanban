package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleEndpoint(HttpExchange exchange,
                                  Endpoint endpoint,
                                  String[] pathParts
    ) throws IOException {
        switch (endpoint) {
            case GET_ALL -> sendText(exchange, getSubtasks());
            case GET -> sendText(exchange, getSubtaskById(getId(pathParts)));
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
        SubTask subTask = parseSubtaskFromRequest(exchange);
        return subTask.getId() == 0 ? createSubtask(subTask) : updateSubtask(subTask);
    }

    SubTask parseSubtaskFromRequest(HttpExchange exchange) throws
            IOException {
        String body = new String(exchange.getRequestBody()
                                         .readAllBytes(),
                                 StandardCharsets.UTF_8);
        return gson.fromJson(body, SubTask.class);
    }

    private String getSubtasks() {
        return gson.toJson(taskManager.getAllSubtask());
    }

    private String getSubtaskById(int id) {
        return gson.toJson(taskManager.getSubtaskById(id));
    }

    private String createSubtask(SubTask subTask) {
        SubTask added = taskManager.createSubTask(subTask);
        return gson.toJson(added);
    }

    private String updateSubtask(SubTask subTask) {
        SubTask updated = taskManager.updateSubTask(subTask);
        return gson.toJson(updated);
    }

    private void deleteAllSubtask() {
        taskManager.deleteAllSubtask();
    }

    private void deleteSubtaskById(int id) {
        taskManager.deleteSubtaskById(id);
    }

    int getId(String[] pathParts) {
        return Integer.parseInt(pathParts[1]);
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        deleteAllSubtask();
        sendEmptyResponseWithCode(exchange, StatusCodes.SUCCESS.code);
    }

    private void handleDelete(HttpExchange exchange, int id) throws
            IOException {
        deleteSubtaskById(id);
        sendEmptyResponseWithCode(exchange, StatusCodes.SUCCESS.code);
    }
}
