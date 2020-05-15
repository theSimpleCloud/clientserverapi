package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.*
import java.lang.reflect.Type

class JsonDataSerializer : JsonSerializer<JsonData>, JsonDeserializer<JsonData> {



    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonData {
        return JsonData.fromJsonElement(json)
    }

    override fun serialize(src: JsonData, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return src.jsonElement
    }


}