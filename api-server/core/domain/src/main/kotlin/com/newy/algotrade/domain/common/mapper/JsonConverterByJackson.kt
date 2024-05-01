package com.newy.algotrade.domain.common.mapper

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

class JsonConverterByJackson(private val mapper: ObjectMapper) : JsonConverter {
    override fun toJson(source: Any): String =
        mapper.writeValueAsString(source)

    override fun <T : Any> _toObject(source: String, type: KClass<T>, extraValues: Map<String, Any>): T {
        return when (type.java) {
            Unit::class.java -> Unit as T
            String::class.java -> source as T
            else -> mapper
                .readerFor(type.java)
                .with(if (extraValues.isNotEmpty()) InjectableValues.Std(extraValues) else null)
                .readValue(source)
        }
    }
}