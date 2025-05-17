package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EpicDeserializer implements JsonDeserializer<EpicTask> {
    @Override
    public EpicTask deserialize(JsonElement jsonElement,
                                Type type,
                                JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();

        EpicTask epicTask = new EpicTask("Deserialized TECHNICAL");

        if (jsonObj.has("id")) {
            epicTask.setId(jsonObj.get("id")
                                  .getAsInt());
        }
        if (jsonObj.has("name")) {
            epicTask.setName(jsonObj.get("name")
                                    .getAsString());
        }
        if (jsonObj.has("description")) {
            epicTask.setDescription(jsonObj.get("description")
                                           .getAsString());
        }
        if (jsonObj.has("status")) {
            epicTask.setStatus(context.deserialize(jsonObj.get("status"),
                                                   Statuses.class));
        }
        if (jsonObj.has("startTime")) {
            epicTask.setStartTime((LocalDateTime) context.deserialize(
                    jsonObj.get("startTime"), LocalDateTime.class));
        }

        if (jsonObj.has("endTime")) {
            epicTask.setEndTime(context.deserialize(jsonObj.get("endTime"),
                                                LocalDateTime.class));
        }

        if (jsonObj.has("duration")) {
            epicTask.setDuration(context.deserialize(jsonObj.get("duration"),
                                                     Duration.class));
        }

        Set<Integer> subtaskIds = new HashSet<>();
        if (jsonObj.has("setOfSubtasksID") &&
            jsonObj.get("setOfSubtasksID")
                   .isJsonArray()) {
            JsonArray subtasksArray = jsonObj.getAsJsonArray(
                    "setOfSubtasksID");
            for (JsonElement element : subtasksArray) {
                subtaskIds.add(element.getAsInt());
            }
        }
        epicTask.setSubtasksId(subtaskIds);

        return epicTask;
    }
}