package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EpicDeserializerTest {

    private EpicDeserializer epicDeserializer;
    private JsonDeserializationContext context;
    private Gson gson;
    private LocalDateTime testTime;
    private Duration testDuration;

    @BeforeEach
    void setUp() {
        epicDeserializer = new EpicDeserializer();
        context = mock(JsonDeserializationContext.class);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        testTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        testDuration = Duration.ofHours(2);
    }

    @Test
    void deserialize_ShouldCreateEpicWithDefaultValues() {
        JsonObject jsonObject = new JsonObject();
        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertEquals("Deserialized TECHNICAL", epic.getName());
        assertNull(epic.getDescription());
        assertEquals(Statuses.NEW, epic.getStatus());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(Duration.ZERO, epic.getDuration());
        assertTrue(epic.getSubtasksIDs().isEmpty());
    }

    @Test
    void deserialize_ShouldSetAllFieldsWhenPresent() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", 1);
        jsonObject.addProperty("name", "Test Epic");
        jsonObject.addProperty("description", "Test Description");
        jsonObject.add("status", gson.toJsonTree(Statuses.IN_PROGRESS));
        jsonObject.add("startTime", gson.toJsonTree(testTime));
        jsonObject.add("endTime", gson.toJsonTree(testTime.plusHours(2)));
        jsonObject.add("duration", gson.toJsonTree(testDuration));

        JsonArray subtasksArray = new JsonArray();
        subtasksArray.add(101);
        subtasksArray.add(102);
        jsonObject.add("setOfSubtasksID", subtasksArray);

        when(context.deserialize(any(JsonElement.class), eq(Statuses.class)))
                .thenReturn(Statuses.IN_PROGRESS);
        when(context.deserialize(any(JsonElement.class), eq(LocalDateTime.class)))
                .thenReturn(testTime)
                .thenReturn(testTime.plusHours(2));
        when(context.deserialize(any(JsonElement.class), eq(Duration.class)))
                .thenReturn(testDuration);

        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertEquals(1, epic.getId());
        assertEquals("Test Epic", epic.getName());
        assertEquals("Test Description", epic.getDescription());
        assertEquals(Statuses.IN_PROGRESS, epic.getStatus());
        assertEquals(testTime, epic.getStartTime());
        assertEquals(testTime.plusHours(2), epic.getEndTime());
        assertEquals(testDuration, epic.getDuration());
        assertEquals(Set.of(101, 102), epic.getSubtasksIDs());
    }

    @Test
    void deserialize_ShouldHandleSubtaskIds() {
        JsonObject jsonObject = new JsonObject();
        JsonArray subtasksArray = new JsonArray();
        subtasksArray.add(101);
        subtasksArray.add(102);
        subtasksArray.add(103);
        jsonObject.add("setOfSubtasksID", subtasksArray);

        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertEquals(3, epic.getSubtasksIDs().size());
        assertTrue(epic.getSubtasksIDs().contains(101));
        assertTrue(epic.getSubtasksIDs().contains(102));
        assertTrue(epic.getSubtasksIDs().contains(103));
    }

    @Test
    void deserialize_ShouldHandleEmptySubtaskIds() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("setOfSubtasksID", new JsonArray());

        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertTrue(epic.getSubtasksIDs().isEmpty());
    }

    @Test
    void deserialize_ShouldIgnoreMissingSubtaskIds() {
        JsonObject jsonObject = new JsonObject();

        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertTrue(epic.getSubtasksIDs().isEmpty());
    }

    @Test
    void deserialize_ShouldHandleInvalidSubtaskIds() {
        JsonObject jsonObject = new JsonObject();
        JsonArray subtasksArray = new JsonArray();
        subtasksArray.add("invalid");
        jsonObject.add("setOfSubtasksID", subtasksArray);

        assertThrows(NumberFormatException.class, () ->
                epicDeserializer.deserialize(jsonObject, null, context));
    }

    @Test
    void deserialize_ShouldHandleTimeFields() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("startTime", gson.toJsonTree(testTime));
        jsonObject.add("endTime", gson.toJsonTree(testTime.plusHours(1)));

        when(context.deserialize(any(JsonElement.class), eq(LocalDateTime.class)))
                .thenReturn(testTime)
                .thenReturn(testTime.plusHours(1));

        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertEquals(testTime, epic.getStartTime());
        assertEquals(testTime.plusHours(1), epic.getEndTime());
    }

    @Test
    void deserialize_ShouldHandleDuration() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("duration", gson.toJsonTree(testDuration));

        when(context.deserialize(any(JsonElement.class), eq(Duration.class)))
                .thenReturn(testDuration);

        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertEquals(testDuration, epic.getDuration());
    }

    @Test
    void deserialize_ShouldHandlePartialFields() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "Partial Epic");
        jsonObject.addProperty("description", "Partial Description");

        EpicTask epic = epicDeserializer.deserialize(jsonObject, null, context);

        assertEquals("Partial Epic", epic.getName());
        assertEquals("Partial Description", epic.getDescription());
        assertEquals(Statuses.NEW, epic.getStatus());
        assertNull(epic.getStartTime());
        assertNull(epic.getEndTime());
        assertEquals(Duration.ZERO, epic.getDuration());
        assertTrue(epic.getSubtasksIDs().isEmpty());
    }
}
