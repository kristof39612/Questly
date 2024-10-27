package hu.bme.aut.szoftverarch.questly.data.utils

import com.google.gson.*
import hu.bme.aut.szoftverarch.questly.data.tasks.*
import java.lang.reflect.Type

class TaskTypeAdapter : JsonSerializer<Task>, JsonDeserializer<Task> {
    override fun serialize(src: Task, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", src::class.java.simpleName)

        // Serialize properties manually based on the Task subtype
        val properties = context.serialize(src, src::class.java).asJsonObject
        for ((key, value) in properties.entrySet()) {
            jsonObject.add(key, value)
        }

        return jsonObject
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Task {
        val jsonObject = json.asJsonObject
        val typeElement = jsonObject.get("type")?.asString
            ?: throw JsonParseException("Invalid or missing 'type' property in JSON: $jsonObject")

        // Remove "type" property to isolate "properties" for deserialization
        jsonObject.remove("type")

        return when (typeElement) {
            TextPromptTask::class.java.simpleName -> context.deserialize(jsonObject, TextPromptTask::class.java)
            GoToPointTask::class.java.simpleName -> context.deserialize(jsonObject, GoToPointTask::class.java)
            SingleChoiceTask::class.java.simpleName -> context.deserialize(jsonObject, SingleChoiceTask::class.java)
            else -> throw JsonParseException("Unknown task type: $typeElement")
        }
    }


}