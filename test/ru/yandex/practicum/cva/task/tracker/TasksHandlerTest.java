package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TasksHandlerTest {

    private TasksHandler tasksHandler;
    private TaskManager mockTaskManager;
    private HttpExchange mockExchange;
    private Gson gson;
    private Headers mockHeaders;

    @BeforeEach
    void setUp() {
        mockTaskManager = mock(TaskManager.class);
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                                     new LocalDateTimeAdapter())
                                .registerTypeAdapter(Duration.class,
                                                     new DurationAdapter())
                                .create();
        tasksHandler = new TasksHandler(mockTaskManager, gson);
        mockExchange = mock(HttpExchange.class);
        mockHeaders = mock(Headers.class);

        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);
    }

    @Test
    void handleEndpoint_GetAllTasks() throws IOException {

        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/tasks"));

        Task task1 = new Task("Task 1");
        Task task2 = new Task("Task 2");
        when(mockTaskManager.getAllTask()).thenReturn(List.of(task1, task2));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        tasksHandler.handleEndpoint(mockExchange, Endpoint.GET_ALL,
                                    new String[]{"tasks"});


        String expected = gson.toJson(List.of(task1, task2));
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_GetTaskById() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/tasks/1"));

        Task task = new Task("Task 1");
        task.setId(1);
        when(mockTaskManager.getTaskById(1)).thenReturn(task);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        tasksHandler.handleEndpoint(mockExchange, Endpoint.GET,
                                    new String[]{"tasks", "1"});

        String expected = gson.toJson(task);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_PostCreateTask() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/tasks"));

        Task newTask = new Task("New Task");
        String requestBody = gson.toJson(newTask);
        when(mockExchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(requestBody.getBytes()));

        Task createdTask = new Task("New Task");
        createdTask.setId(1);
        when(mockTaskManager.createTask(any(Task.class))).thenReturn(
                createdTask);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        tasksHandler.handleEndpoint(mockExchange, Endpoint.POST,
                                    new String[]{"tasks"});

        String expected = gson.toJson(createdTask);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(201,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_PostUpdateTask() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/tasks"));

        Task existingTask = new Task("Existing Task");
        existingTask.setId(1);
        String requestBody = gson.toJson(existingTask);
        when(mockExchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(requestBody.getBytes()));

        Task updatedTask = new Task("Updated Task");
        updatedTask.setId(1);
        when(mockTaskManager.updateTask(any(Task.class))).thenReturn(
                updatedTask);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        tasksHandler.handleEndpoint(mockExchange, Endpoint.POST,
                                    new String[]{"tasks"});

        String expected = gson.toJson(updatedTask);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(201,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_DeleteAllTasks() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("DELETE");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/tasks"));

        tasksHandler.handleEndpoint(mockExchange, Endpoint.DELETE_ALL,
                                    new String[]{"tasks"});

        verify(mockTaskManager).deleteAllTask();
        verify(mockExchange).sendResponseHeaders(200, 0);
    }

    @Test
    void handleEndpoint_DeleteTaskById() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("DELETE");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/tasks/1"));

        tasksHandler.handleEndpoint(mockExchange, Endpoint.DELETE,
                                    new String[]{"tasks", "1"});

        verify(mockTaskManager).deleteTaskById(1);
        verify(mockExchange).sendResponseHeaders(200, 0);
    }

    @Test
    void handleEndpoint_UnknownEndpoint() {
        when(mockExchange.getRequestMethod()).thenReturn("PUT");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/tasks"));

        assertThrows(NonExistentEntityException.class,
                     () -> tasksHandler.handleEndpoint(mockExchange,
                                                       Endpoint.UNKNOWN,
                                                       new String[]{"tasks"}));
    }

    @Test
    void parseTaskFromRequest_ShouldReturnTask() throws IOException {
        Task expectedTask = new Task("Test Task");
        expectedTask.setDescription("Test Description");
        String requestBody = gson.toJson(expectedTask);

        when(mockExchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(requestBody.getBytes()));

        Task actualTask = tasksHandler.parseTaskFromRequest(mockExchange);

        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(),
                     actualTask.getDescription());
    }

    @Test
    void getId_ShouldParseValidId() {
        String[] pathParts = {"tasks", "123"};

        int id = tasksHandler.getId(pathParts);

        assertEquals(123, id);
    }

    @Test
    void getId_ShouldThrowOnInvalidId() {
        String[] pathParts = {"tasks", "invalid"};

        assertThrows(NumberFormatException.class,
                     () -> tasksHandler.getId(pathParts));
    }
}