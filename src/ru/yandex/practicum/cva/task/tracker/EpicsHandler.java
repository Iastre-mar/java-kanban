package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleEndpoint(HttpExchange exchange,
                                  Endpoint endpoint,
                                  String[] pathParts
    ) throws IOException {
        switch (endpoint) {
            case GET_ALL -> sendText(exchange, getEpics());
            case GET -> sendText(exchange, getEpicById(getId(pathParts)));
            case GET_EPIC_SUBTASKS ->
                    sendText(exchange, getEpicSubtasks(getId(pathParts)));
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
        EpicTask epic = parseEpicFromRequest(exchange);
        return epic.getId() == 0 ? createEpic(epic) : updateEpic(epic);
    }

    EpicTask parseEpicFromRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody()
                                         .readAllBytes(),
                                 StandardCharsets.UTF_8);
        return gson.fromJson(body, EpicTask.class);
    }

    private String getEpics() {
        return gson.toJson(taskManager.getAllEpic());
    }

    private String getEpicById(int id) {
        return gson.toJson(taskManager.getEpicById(id));
    }

    private String getEpicSubtasks(int id) {
        return gson.toJson(taskManager.getTasksOfEpic(id));
    }

    private String createEpic(EpicTask epic) {
        EpicTask added = taskManager.createEpic(epic);
        return gson.toJson(added);
    }

    private String updateEpic(EpicTask epic) {
        EpicTask updated = taskManager.updateEpic(epic);
        return gson.toJson(updated);
    }

    private void deleteAllEpic() {
        taskManager.deleteAllEpic();
    }

    private void deleteEpicById(int id) {
        taskManager.deleteEpicById(id);
    }

    private int getId(String[] pathParts) {
        return Integer.parseInt(pathParts[1]);
    }

    private void handleDeleteAll(HttpExchange exchange) throws IOException {
        deleteAllEpic();
        sendEmptyResponseWithCode(exchange, StatusCodes.SUCCESS.code);
    }

    private void handleDelete(HttpExchange exchange, int id) throws
            IOException {
        deleteEpicById(id);
        sendEmptyResponseWithCode(exchange, StatusCodes.SUCCESS.code);
    }
}
