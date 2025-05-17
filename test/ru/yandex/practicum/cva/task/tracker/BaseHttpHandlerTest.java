package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BaseHttpHandlerTest {

    private TaskManager mockTaskManager;
    private Gson gson;
    private BaseHttpHandler handler;
    private HttpExchange mockExchange;
    private Headers mockHeaders;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        mockTaskManager = mock(TaskManager.class);
        gson = new Gson();
        handler = new TestBaseHttpHandler(mockTaskManager, gson);
        mockExchange = mock(HttpExchange.class);
        mockHeaders = mock(Headers.class);
        outputStream = new ByteArrayOutputStream();

        // Stub common methods
        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);
        when(mockExchange.getResponseBody()).thenReturn(outputStream);
    }

    @Test
    void testSend() throws IOException {
        BaseHttpHandler.send(mockExchange, "test response", 200);

        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200,
                                                 "test response".getBytes().length);
        assertEquals("test response", outputStream.toString());
    }

    @Test
    void testSendError() throws IOException {
        BaseHttpHandler.sendError(mockExchange, "error message", 404);

        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(404,
                                                 "error message".getBytes().length);
        assertEquals("error message", outputStream.toString());
    }

    @Test
    void testSendText() throws IOException {
        handler.sendText(mockExchange, "test text");

        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200,
                                                 "test text".getBytes().length);
        assertEquals("test text", outputStream.toString());
    }

    @Test
    void testSendEmptyResponseWithCode() throws IOException {
        handler.sendEmptyResponseWithCode(mockExchange, 204);

        verify(mockExchange).sendResponseHeaders(204, 0);
        verify(mockExchange).close();
    }

    @Test
    void testGetPathParts() {
        String[] parts = handler.getPathParts("/api/v1/tasks/123");
        assertArrayEquals(new String[]{"api", "v1", "tasks", "123"}, parts);

        parts = handler.getPathParts("/");
        assertArrayEquals(new String[]{}, parts);

        parts = handler.getPathParts("//tasks//123//");
        assertArrayEquals(new String[]{"tasks", "123"}, parts);
    }

    @Test
    void testGetTaskEndpoint() throws URISyntaxException {
        // Test GET endpoints
        HttpExchange exchange = createMockExchange("GET", "/tasks");
        assertEquals(Endpoint.GET_ALL, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));

        exchange = createMockExchange("GET", "/tasks/123");
        assertEquals(Endpoint.GET, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));

        exchange = createMockExchange("GET", "/epics/123/subtasks");
        assertEquals(Endpoint.GET_EPIC_SUBTASKS, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));

        // Test POST endpoints
        exchange = createMockExchange("POST", "/tasks");
        assertEquals(Endpoint.POST, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));

        exchange = createMockExchange("POST", "/tasks/123");
        assertEquals(Endpoint.UNKNOWN, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));

        // Test DELETE endpoints
        exchange = createMockExchange("DELETE", "/tasks");
        assertEquals(Endpoint.DELETE_ALL, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));

        exchange = createMockExchange("DELETE", "/tasks/123");
        assertEquals(Endpoint.DELETE, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));

        // Test unknown method
        exchange = createMockExchange("PUT", "/tasks");
        assertEquals(Endpoint.UNKNOWN, handler.getTaskEndpoint(
                exchange.getRequestURI()
                        .getPath(), exchange.getRequestMethod()));
    }

    @Test
    void testIsEpicSubtasks() {
        assertEquals(3, handler.getPathParts("/epics/123/subtasks").length);
        assertTrue(handler.isEpicSubtasks(
                new String[]{"epics", "123", "subtasks"}));
        assertFalse(handler.isEpicSubtasks(
                new String[]{"tasks", "123", "subtasks"}));
        assertFalse(handler.isEpicSubtasks(
                new String[]{"epics", "abc", "subtasks"}));
        // 500 ошибка, все верно
        assertThrows(ArrayIndexOutOfBoundsException.class,
                     () -> handler.isEpicSubtasks(
                             new String[]{"epics", "123"}));
    }

    @Test
    void testIsInteger() {
        assertTrue(handler.isInteger("123"));
        assertTrue(handler.isInteger("0"));
        assertTrue(handler.isInteger("-123"));
        assertFalse(handler.isInteger("abc"));
        assertFalse(handler.isInteger("123.45"));
        assertFalse(handler.isInteger(""));
    }

    @Test
    void testHandleWithNonExistentTaskException() throws
            IOException,
            URISyntaxException {
        BaseHttpHandler throwingHandler = new TestBaseHttpHandler(
                mockTaskManager, gson) {
            @Override
            protected void handleEndpoint(HttpExchange exchange,
                                          Endpoint endpoint,
                                          String[] pathParts
            ) throws IOException {
                throw new NonExistentTaskException("Task not found");
            }
        };

        HttpExchange exchange = createMockExchangeWithResponseBody("GET",
                                                                   "/tasks/123");
        throwingHandler.handle(exchange);

        ArgumentCaptor<Integer> codeCaptor = ArgumentCaptor.forClass(
                Integer.class);
        verify(exchange).sendResponseHeaders(codeCaptor.capture(), anyLong());
        assertEquals(StatusCodes.NOT_FOUND.code, codeCaptor.getValue());
    }

    @Test
    void testHandleWithTaskOverlapException() throws
            IOException,
            URISyntaxException {
        BaseHttpHandler throwingHandler = new TestBaseHttpHandler(
                mockTaskManager, gson) {
            @Override
            protected void handleEndpoint(HttpExchange exchange,
                                          Endpoint endpoint,
                                          String[] pathParts
            ) throws IOException {
                throw new TaskOverlapException("Tasks overlap");
            }
        };

        HttpExchange exchange = createMockExchangeWithResponseBody("GET",
                                                                   "/tasks/123");
        throwingHandler.handle(exchange);

        ArgumentCaptor<Integer> codeCaptor = ArgumentCaptor.forClass(
                Integer.class);
        verify(exchange).sendResponseHeaders(codeCaptor.capture(), anyLong());
        assertEquals(StatusCodes.NOT_ACCEPTABLE.code, codeCaptor.getValue());
    }

    @Test
    void testHandleWithGenericException() throws
            IOException,
            URISyntaxException {
        BaseHttpHandler throwingHandler = new TestBaseHttpHandler(
                mockTaskManager, gson) {
            @Override
            protected void handleEndpoint(HttpExchange exchange,
                                          Endpoint endpoint,
                                          String[] pathParts
            ) throws IOException {
                throw new RuntimeException("Unexpected error");
            }
        };

        HttpExchange exchange = createMockExchangeWithResponseBody("GET",
                                                                   "/tasks/123");
        throwingHandler.handle(exchange);

        ArgumentCaptor<Integer> codeCaptor = ArgumentCaptor.forClass(
                Integer.class);
        verify(exchange).sendResponseHeaders(codeCaptor.capture(), anyLong());
        assertEquals(StatusCodes.INTERNAL_ERROR.code, codeCaptor.getValue());
    }

    private HttpExchange createMockExchange(String method, String path) throws
            URISyntaxException {
        HttpExchange exchange = mock(HttpExchange.class);
        Headers headers = mock(Headers.class);

        when(exchange.getRequestMethod()).thenReturn(method);
        when(exchange.getRequestURI()).thenReturn(new URI(path));
        when(exchange.getResponseHeaders()).thenReturn(headers);

        return exchange;
    }

    private HttpExchange createMockExchangeWithResponseBody(String method,
                                                            String path
    ) throws URISyntaxException {
        HttpExchange exchange = createMockExchange(method, path);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);
        return exchange;
    }

    private static class TestBaseHttpHandler extends BaseHttpHandler {
        TestBaseHttpHandler(TaskManager taskManager, Gson gson) {
            super(taskManager, gson);
        }

        @Override
        protected void handleEndpoint(HttpExchange exchange,
                                      Endpoint endpoint,
                                      String[] pathParts
        ) throws IOException {
            // Потому что абстрактный
        }
    }
}