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

class SubtasksHandlerTest {

    private SubtasksHandler subtasksHandler;
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
        subtasksHandler = new SubtasksHandler(mockTaskManager, gson);
        mockExchange = mock(HttpExchange.class);
        mockHeaders = mock(Headers.class);

        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);
    }

    @Test
    void handleEndpoint_GetAllSubtasks() throws IOException {

        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/subtasks"));

        SubTask subTask1 = new SubTask("Subtask 1");
        SubTask subTask2 = new SubTask("Subtask 2");
        when(mockTaskManager.getAllSubtask()).thenReturn(
                List.of(subTask1, subTask2));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        subtasksHandler.handleEndpoint(mockExchange, Endpoint.GET_ALL,
                                       new String[]{"subtasks"});

        String expected = gson.toJson(List.of(subTask1, subTask2));
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_GetSubtaskById() throws IOException {

        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(
                URI.create("/subtasks/1"));

        SubTask subTask = new SubTask("Subtask 1");
        subTask.setId(1);
        when(mockTaskManager.getSubtaskById(1)).thenReturn(subTask);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        subtasksHandler.handleEndpoint(mockExchange, Endpoint.GET,
                                       new String[]{"subtasks", "1"});

        String expected = gson.toJson(subTask);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_PostCreateSubtask() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/subtasks"));

        SubTask newSubTask = new SubTask("New Subtask");
        String requestBody = gson.toJson(newSubTask);
        when(mockExchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(requestBody.getBytes()));

        SubTask createdSubTask = new SubTask("New Subtask");
        createdSubTask.setId(1);
        when(mockTaskManager.createSubTask(any(SubTask.class))).thenReturn(
                createdSubTask);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        subtasksHandler.handleEndpoint(mockExchange, Endpoint.POST,
                                       new String[]{"subtasks"});

        String expected = gson.toJson(createdSubTask);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(201,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_PostUpdateSubtask() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/subtasks"));

        SubTask existingSubTask = new SubTask("Existing Subtask");
        existingSubTask.setId(1);
        String requestBody = gson.toJson(existingSubTask);
        when(mockExchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(requestBody.getBytes()));

        SubTask updatedSubTask = new SubTask("Updated Subtask");
        updatedSubTask.setId(1);
        when(mockTaskManager.updateSubTask(any(SubTask.class))).thenReturn(
                updatedSubTask);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        subtasksHandler.handleEndpoint(mockExchange, Endpoint.POST,
                                       new String[]{"subtasks"});

        String expected = gson.toJson(updatedSubTask);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type",
                                "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(201,
                                                 expected.getBytes().length);
    }

    @Test
    void handleEndpoint_DeleteAllSubtasks() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("DELETE");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/subtasks"));

        subtasksHandler.handleEndpoint(mockExchange, Endpoint.DELETE_ALL,
                                       new String[]{"subtasks"});

        verify(mockTaskManager).deleteAllSubtask();
        verify(mockExchange).sendResponseHeaders(200, 0);
    }

    @Test
    void handleEndpoint_DeleteSubtaskById() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("DELETE");
        when(mockExchange.getRequestURI()).thenReturn(
                URI.create("/subtasks/1"));

        subtasksHandler.handleEndpoint(mockExchange, Endpoint.DELETE,
                                       new String[]{"subtasks", "1"});

        verify(mockTaskManager).deleteSubtaskById(1);
        verify(mockExchange).sendResponseHeaders(200, 0);
    }

    @Test
    void handleEndpoint_UnknownEndpoint() {
        when(mockExchange.getRequestMethod()).thenReturn("PUT");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/subtasks"));

        assertThrows(NonExistentEntityException.class,
                     () -> subtasksHandler.handleEndpoint(mockExchange,
                                                          Endpoint.UNKNOWN,
                                                          new String[]{
                                                                  "subtasks"
                                                          }));
    }

    @Test
    void parseSubtaskFromRequest_ShouldReturnSubtask() throws IOException {
        SubTask expectedSubTask = new SubTask("Test Subtask");
        expectedSubTask.setDescription("Test Description");
        expectedSubTask.setParentId(1);
        String requestBody = gson.toJson(expectedSubTask);

        when(mockExchange.getRequestBody()).thenReturn(
                new ByteArrayInputStream(requestBody.getBytes()));

        SubTask actualSubTask = subtasksHandler.parseSubtaskFromRequest(
                mockExchange);

        assertEquals(expectedSubTask.getName(), actualSubTask.getName());
        assertEquals(expectedSubTask.getDescription(),
                     actualSubTask.getDescription());
        assertEquals(expectedSubTask.getParentId(),
                     actualSubTask.getParentId());
    }

    @Test
    void getId_ShouldParseValidId() {
        String[] pathParts = {"subtasks", "123"};

        int id = subtasksHandler.getId(pathParts);

        assertEquals(123, id);
    }

    @Test
    void getId_ShouldThrowOnInvalidId() {
        String[] pathParts = {"subtasks", "invalid"};

        assertThrows(NumberFormatException.class,
                     () -> subtasksHandler.getId(pathParts));
    }
}
