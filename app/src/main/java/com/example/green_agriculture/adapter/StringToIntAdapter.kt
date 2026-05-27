package com.example.green_agriculture.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class StringToIntAdapter : JsonDeserializer<Int> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): Int? {
        return when (json?.isJsonPrimitive) {
            true if json.asJsonPrimitive.isString -> json.asString.toInt()
            true if json.asJsonPrimitive.isNumber -> json.asInt
            else -> throw JsonParseException("Unexpected type for $typeOfT")
        }
    }
}