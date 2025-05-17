package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final String requestPath = exchange.getRequestURI().getPath();
            final String requestMethod = exchange.getRequestMethod();
            final String[] pathParts = getPathParts(requestPath);

            final Endpoint endpoint = getTaskEndpoint(requestPath, requestMethod);
            handleEndpoint(exchange, endpoint, pathParts);
        } catch (NonExistentTaskException e) {
            sendError(exchange, e.getMessage(), StatusCodes.NOT_FOUND.code);
        } catch (TaskOverlapException e) {
            sendError(exchange, e.getMessage(), StatusCodes.NOT_ACCEPTABLE.code);
        } catch (Exception e) {
            sendError(exchange, e.getMessage(), StatusCodes.INTERNAL_ERROR.code);
        }
    }

    public static void send(HttpExchange exchange,
                            String text,
                            int code
    ) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders()
                .add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, response.length);
        exchange.getResponseBody()
                .write(response);
        exchange.close();
    }

    public static void sendError(HttpExchange exchange,
                                 String text,
                                 int code
    ) throws IOException {
        JsonObject error = new JsonObject();
        error.addProperty("message", text);

        send(exchange, text, code);
    }

    protected abstract void handleEndpoint(HttpExchange exchange, Endpoint endpoint, String[] pathParts)
            throws IOException;

    protected void sendText(HttpExchange exchange, String text) throws
            IOException {
        send(exchange, text, StatusCodes.SUCCESS.code);
    }

    protected void sendEmptyResponseWithCode(HttpExchange exchange,
                                             int code
    ) throws IOException {
        exchange.sendResponseHeaders(code, 0);
        exchange.close();
    }

    protected Endpoint getTaskEndpoint(String requestPath,
                                       String requestMethod
    ) {
        String[] pathParts = getPathParts(requestPath);
        return switch (requestMethod) {
            case "GET" -> resolveGetEndpoint(pathParts);
            case "POST" -> resolvePostEndpoint(pathParts);
            case "DELETE" -> resolveDeleteEndpoint(pathParts);
            default -> Endpoint.UNKNOWN;
        };
    }

    protected String[] getPathParts(String requestPath) {
        return Arrays.stream(requestPath.split("/"))
                     .filter(s -> !s.isEmpty())
                     .toArray(String[]::new);
    }

    private Endpoint resolveGetEndpoint(String[] pathParts) {
        return switch (pathParts.length) {
            case 1 -> Endpoint.GET_ALL;
            case 2 ->
                    isInteger(pathParts[1]) ? Endpoint.GET : Endpoint.UNKNOWN;
            case 3 -> isEpicSubtasks(
                    pathParts) ? Endpoint.GET_EPIC_SUBTASKS : Endpoint.UNKNOWN;
            default -> Endpoint.UNKNOWN;
        };
    }

    private Endpoint resolvePostEndpoint(String[] pathParts) {
        return pathParts.length == 1 ? Endpoint.POST : Endpoint.UNKNOWN;
    }

    private Endpoint resolveDeleteEndpoint(String[] pathParts) {
        return switch (pathParts.length) {
            case 1 -> Endpoint.DELETE_ALL;
            case 2 -> isInteger(
                    pathParts[1]) ? Endpoint.DELETE : Endpoint.UNKNOWN;
            default -> Endpoint.UNKNOWN;
        };
    }

    boolean isEpicSubtasks(String[] pathParts) {
        return "epics".equals(pathParts[0]) &&
               "subtasks".equals(pathParts[2]) &&
               isInteger(pathParts[1]);
    }

    boolean isInteger(String str) {
        boolean res = false;
        try {
            Integer.parseInt(str);
            res = true;
        } catch (NumberFormatException e) {
            //pass
        }
        return res;
    }
}