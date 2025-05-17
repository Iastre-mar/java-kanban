package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubTaskDeserializerTest {

    private SubTaskDeserializer subTaskDeserializer;
    private JsonDeserializationContext context;
    private Gson gson;
    private LocalDateTime testTime;
    private Duration testDuration;

    @BeforeEach
    void setUp() {
        subTaskDeserializer = new SubTaskDeserializer();
        context = mock(JsonDeserializationContext.class);
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class,
                                                     new LocalDateTimeAdapter())
                                .registerTypeAdapter(Duration.class,
                                                     new DurationAdapter())
                                .create();

        testTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        testDuration = Duration.ofHours(2);
    }

    @Test
    void deserialize_ShouldCreateSubTaskWithDefaultValues() {
        JsonObject jsonObject = new JsonObject();
        SubTask subTask = subTaskDeserializer.deserialize(jsonObject, null,
                                                          context);

        assertEquals("Deserialized TECHNICAL", subTask.getName());
        assertNull(subTask.getDescription());
        assertEquals(Statuses.NEW, subTask.getStatus());
        assertNull(subTask.getStartTime());
        assertEquals(Duration.ZERO, subTask.getDuration());
        assertEquals(0, subTask.getParentId());
    }

    @Test
    void deserialize_ShouldSetAllFieldsWhenPresent() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", 1);
        jsonObject.addProperty("name", "Test SubTask");
        jsonObject.addProperty("description", "Test Description");
        jsonObject.add("status", gson.toJsonTree(Statuses.IN_PROGRESS));
        jsonObject.add("startTime", gson.toJsonTree(testTime));
        jsonObject.add("duration", gson.toJsonTree(testDuration));
        jsonObject.addProperty("parentId", 101);

        when(context.deserialize(any(JsonElement.class),
                                 eq(Statuses.class))).thenReturn(
                Statuses.IN_PROGRESS);
        when(context.deserialize(any(JsonElement.class),
                                 eq(LocalDateTime.class))).thenReturn(
                testTime);
        when(context.deserialize(any(JsonElement.class),
                                 eq(Duration.class))).thenReturn(testDuration);

        SubTask subTask = subTaskDeserializer.deserialize(jsonObject, null,
                                                          context);

        assertEquals(1, subTask.getId());
        assertEquals("Test SubTask", subTask.getName());
        assertEquals("Test Description", subTask.getDescription());
        assertEquals(Statuses.IN_PROGRESS, subTask.getStatus());
        assertEquals(testTime, subTask.getStartTime());
        assertEquals(testDuration, subTask.getDuration());
        assertEquals(101, subTask.getParentId());
    }

    @Test
    void deserialize_ShouldHandleParentId() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parentId", 201);

        SubTask subTask = subTaskDeserializer.deserialize(jsonObject, null,
                                                          context);

        assertEquals(201, subTask.getParentId());
    }

    @Test
    void deserialize_ShouldHandleMissingParentId() {
        JsonObject jsonObject = new JsonObject();

        SubTask subTask = subTaskDeserializer.deserialize(jsonObject, null,
                                                          context);

        assertEquals(0, subTask.getParentId());
    }

    @Test
    void deserialize_ShouldHandleTimeFields() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("startTime", gson.toJsonTree(testTime));

        when(context.deserialize(any(JsonElement.class),
                                 eq(LocalDateTime.class))).thenReturn(
                testTime);

        SubTask subTask = subTaskDeserializer.deserialize(jsonObject, null,
                                                          context);

        assertEquals(testTime, subTask.getStartTime());
    }

    @Test
    void deserialize_ShouldHandleDuration() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("duration", gson.toJsonTree(testDuration));

        when(context.deserialize(any(JsonElement.class),
                                 eq(Duration.class))).thenReturn(testDuration);

        SubTask subTask = subTaskDeserializer.deserialize(jsonObject, null,
                                                          context);

        assertEquals(testDuration, subTask.getDuration());
    }

    @Test
    void deserialize_ShouldHandlePartialFields() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "Partial SubTask");
        jsonObject.addProperty("description", "Partial Description");

        SubTask subTask = subTaskDeserializer.deserialize(jsonObject, null,
                                                          context);

        assertEquals("Partial SubTask", subTask.getName());
        assertEquals("Partial Description", subTask.getDescription());
        assertEquals(Statuses.NEW, subTask.getStatus());
        assertNull(subTask.getStartTime());
        assertEquals(Duration.ZERO, subTask.getDuration());
        assertEquals(0, subTask.getParentId());
    }

    @Test
    void deserialize_ShouldHandleInvalidParentId() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parentId", "invalid");

        assertThrows(NumberFormatException.class,
                     () -> subTaskDeserializer.deserialize(jsonObject, null,
                                                           context));
    }
}