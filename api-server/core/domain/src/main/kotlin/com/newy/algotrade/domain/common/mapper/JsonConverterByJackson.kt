package com.newy.algotrade.domain.common.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

class JsonConverterByJackson(private val mapper: ObjectMapper) : JsonConverter {
    override fun toJson(source: Any): String =
        mapper.writeValueAsString(source)

    override fun <T : Any> _toObject(source: String, type: KClass<T>): T =
        when (type.java) {
            Unit::class.java -> Unit as T
            String::class.java -> source as T
            else -> mapper.readValue(source, type.java)
        }
}