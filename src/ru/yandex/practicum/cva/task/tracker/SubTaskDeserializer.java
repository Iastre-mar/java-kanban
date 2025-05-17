package ru.yandex.practicum.cva.task.tracker;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class SubTaskDeserializer  implements JsonDeserializer<SubTask> {
    @Override
    public SubTask deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws
            JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();

        SubTask subTask = new SubTask("Deserialized TECHNICAL");

        if (jsonObj.has("id")) {
            subTask.setId(jsonObj.get("id").getAsInt());
        }
        if (jsonObj.has("name")) {
            subTask.setName(jsonObj.get("name").getAsString());
        }
        if (jsonObj.has("description")) {
            subTask.setDescription(jsonObj.get("description").getAsString());
        }
        if (jsonObj.has("status")) {
            subTask.setStatus(context.deserialize(jsonObj.get("status"), Statuses.class));
        }
        if (jsonObj.has("startTime")) {
            subTask.setStartTime(
                    (LocalDateTime) context.deserialize(jsonObj.get("startTime"), LocalDateTime.class));
        }


        if (jsonObj.has("duration")) {
            subTask.setDuration(
                    context.deserialize(jsonObj.get("duration"), Duration.class));
        }
        if (jsonObj.has("parentId")){
            subTask.setParentId(jsonObj.get("parentId").getAsInt());
        }


        return subTask;
    }
}
