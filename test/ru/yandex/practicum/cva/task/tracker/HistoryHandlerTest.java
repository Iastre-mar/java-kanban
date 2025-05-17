package ru.yandex.practicum.cva.task.tracker;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HistoryHandlerTest {

    private HistoryHandler historyHandler;
    private TaskManager mockTaskManager;
    private HttpExchange mockExchange;
    private Gson gson;
    private Headers mockHeaders;

    @BeforeEach
    void setUp() {
        mockTaskManager = mock(TaskManager.class);
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                                .registerTypeAdapter(Duration.class, new DurationAdapter()).create();
        historyHandler = new HistoryHandler(mockTaskManager, gson);
        mockExchange = mock(HttpExchange.class);
        mockHeaders = mock(Headers.class);

        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);
    }

    @Test
    void handleEndpoint_GetHistory() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/history"));

        Task task1 = new Task("Task 1");
        Task task2 = new Task("Task 2");
        when(mockTaskManager.getHistory()).thenReturn(List.of(task1, task2));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        historyHandler.handleEndpoint(mockExchange, Endpoint.GET_ALL, new String[]{"history"});

        String expected = gson.toJson(List.of(task1, task2));
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_GetHistoryEmpty() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/history"));

        when(mockTaskManager.getHistory()).thenReturn(List.of());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        historyHandler.handleEndpoint(mockExchange, Endpoint.GET_ALL, new String[]{"history"});

        String expected = gson.toJson(List.of());
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_UnknownMethod() {
        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/history"));

        assertThrows(NonExistentEntityException.class, () ->
                historyHandler.handleEndpoint(mockExchange, Endpoint.UNKNOWN, new String[]{"history"}));
    }

    @Test
    void getHistory_ReturnsCorrectJson() {
        // Setup
        Task task1 = new Task("Task 1");
        Task task2 = new Task("Task 2");
        when(mockTaskManager.getHistory()).thenReturn(List.of(task1, task2));

        String result = historyHandler.getHistory();

        String expected = gson.toJson(List.of(task1, task2));
        assertEquals(expected, result);
    }

    @Test
    void getHistory_ReturnsEmptyArrayWhenNoHistory() {
        when(mockTaskManager.getHistory()).thenReturn(List.of());

        String result = historyHandler.getHistory();

        String expected = gson.toJson(List.of());
        assertEquals(expected, result);
    }
}