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

class PrioritizedTasksHandlerTest {

    private PrioritizedTasksHandler prioritizedHandler;
    private TaskManager mockTaskManager;
    private HttpExchange mockExchange;
    private Gson gson;
    private Headers mockHeaders;

    @BeforeEach
    void setUp() {
        mockTaskManager = mock(TaskManager.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        prioritizedHandler = new PrioritizedTasksHandler(mockTaskManager, gson);
        mockExchange = mock(HttpExchange.class);
        mockHeaders = mock(Headers.class);

        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);
    }

    @Test
    void handleEndpoint_GetPrioritizedTasks() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/prioritized"));

        Task task1 = new Task("High priority");
        Task task2 = new Task("Medium priority");
        when(mockTaskManager.getPrioritizedTasks()).thenReturn(List.of(task1, task2));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);


        prioritizedHandler.handleEndpoint(mockExchange, Endpoint.GET_ALL, new String[]{"prioritized"});


        String expected = gson.toJson(List.of(task1, task2));
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_GetPrioritizedTasksEmpty() throws IOException {

        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/prioritized"));

        when(mockTaskManager.getPrioritizedTasks()).thenReturn(List.of());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);


        prioritizedHandler.handleEndpoint(mockExchange, Endpoint.GET_ALL, new String[]{"prioritized"});


        String expected = gson.toJson(List.of());
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_UnknownMethod() {

        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/prioritized"));


        assertThrows(NonExistentEntityException.class, () ->
                prioritizedHandler.handleEndpoint(mockExchange, Endpoint.UNKNOWN, new String[]{"prioritized"}));
    }

    @Test
    void getPrioritizedTasks_ReturnsCorrectJson() {

        Task task1 = new Task("Urgent task");
        Task task2 = new Task("Important task");
        when(mockTaskManager.getPrioritizedTasks()).thenReturn(List.of(task1, task2));


        String result = prioritizedHandler.getPrioritizedTasks();


        String expected = gson.toJson(List.of(task1, task2));
        assertEquals(expected, result);
    }

    @Test
    void getPrioritizedTasks_ReturnsEmptyArrayWhenNoTasks() {

        when(mockTaskManager.getPrioritizedTasks()).thenReturn(List.of());


        String result = prioritizedHandler.getPrioritizedTasks();


        String expected = gson.toJson(List.of());
        assertEquals(expected, result);
    }
}