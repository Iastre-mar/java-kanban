package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext context
    ) throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();

        Task task = new Task("Deserialized TECHNICAL");

        if (jsonObj.has("id")) {
            task.setId(jsonObj.get("id")
                              .getAsInt());
        }
        if (jsonObj.has("name")) {
            task.setName(jsonObj.get("name")
                                .getAsString());
        }
        if (jsonObj.has("description")) {
            task.setDescription(jsonObj.get("description")
                                       .getAsString());
        }
        if (jsonObj.has("status")) {
            task.setStatus(context.deserialize(jsonObj.get("status"),
                                               Statuses.class));
        }
        if (jsonObj.has("startTime")) {
            task.setStartTime((LocalDateTime) context.deserialize(
                    jsonObj.get("startTime"), LocalDateTime.class));
        }


        if (jsonObj.has("duration")) {
            task.setDuration(context.deserialize(jsonObj.get("duration"),
                                                 Duration.class));
        }


        return task;
    }
}