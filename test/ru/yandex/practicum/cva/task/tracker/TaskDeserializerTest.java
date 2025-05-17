package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskDeserializerTest {

    private TaskDeserializer taskDeserializer;
    private JsonDeserializationContext context;
    private Gson gson;

    @BeforeEach
    void setUp() {
        taskDeserializer = new TaskDeserializer();
        context = mock(JsonDeserializationContext.class);
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                                     new LocalDateTimeAdapter())
                                .registerTypeAdapter(Duration.class,
                                                     new DurationAdapter())
                                .create();
    }

    @Test
    void deserialize_ShouldCreateTaskWithDefaultValues() {
        JsonObject jsonObject = new JsonObject();
        Task task = taskDeserializer.deserialize(jsonObject, null, context);

        assertEquals("Deserialized TECHNICAL", task.getName());
        assertNull(task.getDescription());
        assertEquals(Statuses.NEW, task.getStatus());
        assertNull(task.getStartTime());
        assertEquals(Duration.ZERO, task.getDuration());
    }

    @Test
    void deserialize_ShouldSetAllFieldsWhenPresent() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", 1);
        jsonObject.addProperty("name", "Test Task");
        jsonObject.addProperty("description", "Test Description");
        jsonObject.add("status", gson.toJsonTree(Statuses.IN_PROGRESS));
        jsonObject.add("startTime", gson.toJsonTree(LocalDateTime.now()));
        jsonObject.add("duration", gson.toJsonTree(Duration.ofHours(2)));

        when(context.deserialize(any(JsonElement.class),
                                 eq(Statuses.class))).thenReturn(
                Statuses.IN_PROGRESS);
        when(context.deserialize(any(JsonElement.class),
                                 eq(LocalDateTime.class))).thenReturn(
                LocalDateTime.now());
        when(context.deserialize(any(JsonElement.class),
                                 eq(Duration.class))).thenReturn(
                Duration.ofHours(2));

        Task task = taskDeserializer.deserialize(jsonObject, null, context);

        assertEquals(1, task.getId());
        assertEquals("Test Task", task.getName());
        assertEquals("Test Description", task.getDescription());
        assertEquals(Statuses.IN_PROGRESS, task.getStatus());
        assertNotNull(task.getStartTime());
        assertEquals(Duration.ofHours(2), task.getDuration());
    }

    @Test
    void deserialize_ShouldHandlePartialFields() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "Partial Task");
        jsonObject.addProperty("description", "Partial Description");

        Task task = taskDeserializer.deserialize(jsonObject, null, context);

        assertEquals("Partial Task", task.getName());
        assertEquals("Partial Description", task.getDescription());
        assertEquals(Statuses.NEW, task.getStatus());
        assertNull(task.getStartTime());
        assertEquals(Duration.ZERO, task.getDuration());
    }

    @Test
    void deserialize_ShouldHandleStatusEnum() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("status", gson.toJsonTree(Statuses.DONE));

        when(context.deserialize(any(JsonElement.class),
                                 eq(Statuses.class))).thenReturn(
                Statuses.DONE);

        Task task = taskDeserializer.deserialize(jsonObject, null, context);

        assertEquals(Statuses.DONE, task.getStatus());
    }

    @Test
    void deserialize_ShouldHandleDateTime() {
        LocalDateTime now = LocalDateTime.now();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("startTime", gson.toJsonTree(now));

        when(context.deserialize(any(JsonElement.class),
                                 eq(LocalDateTime.class))).thenReturn(now);

        Task task = taskDeserializer.deserialize(jsonObject, null, context);

        assertEquals(now, task.getStartTime());
    }

    @Test
    void deserialize_ShouldHandleDuration() {
        Duration duration = Duration.ofMinutes(45);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("duration", gson.toJsonTree(duration));

        when(context.deserialize(any(JsonElement.class),
                                 eq(Duration.class))).thenReturn(duration);

        Task task = taskDeserializer.deserialize(jsonObject, null, context);

        assertEquals(duration, task.getDuration());
    }

    @Test
    void deserialize_ShouldNotFailOnMissingFields() {
        JsonObject jsonObject = new JsonObject();
        assertDoesNotThrow(
                () -> taskDeserializer.deserialize(jsonObject, null, context));
    }
}
