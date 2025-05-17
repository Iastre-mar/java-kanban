package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleEndpoint(HttpExchange exchange,
                                  Endpoint endpoint,
                                  String[] pathParts
    ) throws IOException {
        switch (endpoint) {
            case GET_ALL -> sendText(exchange, getHistory());
            default -> throw new NonExistentEntityException(
                    "Endpoint not found: %s %s".formatted(
                            exchange.getRequestMethod(),
                            exchange.getRequestURI()
                                    .getPath()));
        }
    }

    String getHistory() {
        return gson.toJson(taskManager.getHistory());
    }

}