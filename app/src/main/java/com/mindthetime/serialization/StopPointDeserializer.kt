package com.mindthetime.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.mindthetime.model.StopPoint
import java.lang.reflect.Type

class StopPointDeserializer : JsonDeserializer<StopPoint> {

    private fun cleanStationName(name: String?): String {
        if (name.isNullOrEmpty()) return ""
        return name.replace("Underground Station", "")
            .replace("DLR Station", "")
            .replace("Rail Station", "")
            .replace("TEST", "")
            .trim()
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): StopPoint {
        val jsonObject = json.asJsonObject

        val id = jsonObject.get("id")?.asString ?: throw JsonParseException("StopPoint missing 'id'")
        val stopType = jsonObject.get("stopType")?.asString ?: throw JsonParseException("StopPoint missing 'stopType'")
        
        // Clean the station name directly, as you suggested.
        val cleanedStationName = cleanStationName(jsonObject.get("commonName")?.asString)

        if (cleanedStationName.isEmpty()) {
            throw JsonParseException("Station name is empty or missing")
        }

        return StopPoint(
            id = id,
            stationName = cleanedStationName,
            stopType = stopType
        )
    }
}