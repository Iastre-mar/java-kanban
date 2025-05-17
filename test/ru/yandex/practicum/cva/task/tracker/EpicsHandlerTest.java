package ru.yandex.practicum.cva.task.tracker;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EpicsHandlerTest {

    private EpicsHandler epicsHandler;
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
        epicsHandler = new EpicsHandler(mockTaskManager, gson);
        mockExchange = mock(HttpExchange.class);
        mockHeaders = mock(Headers.class);

        when(mockExchange.getResponseHeaders()).thenReturn(mockHeaders);
    }

    @Test
    void handleEndpoint_GetAllEpics() throws IOException {

        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics"));

        EpicTask epic1 = new EpicTask("Epic 1");
        EpicTask epic2 = new EpicTask("Epic 2");
        when(mockTaskManager.getAllEpic()).thenReturn(List.of(epic1, epic2));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        epicsHandler.handleEndpoint(mockExchange, Endpoint.GET_ALL, new String[]{"epics"});

        String expected = gson.toJson(List.of(epic1, epic2));
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_GetEpicById() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics/1"));

        EpicTask epic = new EpicTask("Epic 1");
        epic.setId(1);
        when(mockTaskManager.getEpicById(1)).thenReturn(epic);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        epicsHandler.handleEndpoint(mockExchange, Endpoint.GET, new String[]{"epics", "1"});

        String expected = gson.toJson(epic);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_GetEpicSubtasks() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("GET");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics/1/subtasks"));

        SubTask subTask1 = new SubTask("Subtask 1");
        SubTask subTask2 = new SubTask("Subtask 2");
        when(mockTaskManager.getTasksOfEpic(1)).thenReturn(List.of(subTask1, subTask2));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        epicsHandler.handleEndpoint(mockExchange, Endpoint.GET_EPIC_SUBTASKS,
                                    new String[]{"epics", "1", "subtasks"});

        String expected = gson.toJson(List.of(subTask1, subTask2));
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(200, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_PostCreateEpic() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics"));

        EpicTask newEpic = new EpicTask("New Epic");
        String requestBody = gson.toJson(newEpic);
        when(mockExchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(requestBody.getBytes()));

        EpicTask createdEpic = new EpicTask("New Epic");
        createdEpic.setId(1);
        when(mockTaskManager.createEpic(any(EpicTask.class))).thenReturn(createdEpic);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        epicsHandler.handleEndpoint(mockExchange, Endpoint.POST, new String[]{"epics"});

        String expected = gson.toJson(createdEpic);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(201, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_PostUpdateEpic() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("POST");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics"));

        EpicTask existingEpic = new EpicTask("Existing Epic");
        existingEpic.setId(1);
        String requestBody = gson.toJson(existingEpic);
        when(mockExchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(requestBody.getBytes()));

        EpicTask updatedEpic = new EpicTask("Updated Epic");
        updatedEpic.setId(1);
        when(mockTaskManager.updateEpic(any(EpicTask.class))).thenReturn(updatedEpic);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockExchange.getResponseBody()).thenReturn(outputStream);

        epicsHandler.handleEndpoint(mockExchange, Endpoint.POST, new String[]{"epics"});

        String expected = gson.toJson(updatedEpic);
        assertEquals(expected, outputStream.toString());
        verify(mockHeaders).add("Content-Type", "application/json;charset=utf-8");
        verify(mockExchange).sendResponseHeaders(201, expected.getBytes().length);
    }

    @Test
    void handleEndpoint_DeleteAllEpics() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("DELETE");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics"));

        epicsHandler.handleEndpoint(mockExchange, Endpoint.DELETE_ALL, new String[]{"epics"});

        verify(mockTaskManager).deleteAllEpic();
        verify(mockExchange).sendResponseHeaders(200, 0);
    }

    @Test
    void handleEndpoint_DeleteEpicById() throws IOException {
        when(mockExchange.getRequestMethod()).thenReturn("DELETE");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics/1"));

        epicsHandler.handleEndpoint(mockExchange, Endpoint.DELETE, new String[]{"epics", "1"});

        verify(mockTaskManager).deleteEpicById(1);
        verify(mockExchange).sendResponseHeaders(200, 0);
    }

    @Test
    void handleEndpoint_UnknownEndpoint() {
        when(mockExchange.getRequestMethod()).thenReturn("PUT");
        when(mockExchange.getRequestURI()).thenReturn(URI.create("/epics"));

        assertThrows(NonExistentEntityException.class, () ->
                epicsHandler.handleEndpoint(mockExchange, Endpoint.UNKNOWN, new String[]{"epics"}));
    }

    @Test
    void parseEpicFromRequest_ShouldReturnEpic() throws IOException {
        EpicTask expectedEpic = new EpicTask("Test Epic");
        expectedEpic.setDescription("Test Description");
        String requestBody = gson.toJson(expectedEpic);

        when(mockExchange.getRequestBody())
                .thenReturn(new ByteArrayInputStream(requestBody.getBytes()));

        EpicTask actualEpic = epicsHandler.parseEpicFromRequest(mockExchange);

        assertEquals(expectedEpic.getName(), actualEpic.getName());
        assertEquals(expectedEpic.getDescription(), actualEpic.getDescription());
    }
}