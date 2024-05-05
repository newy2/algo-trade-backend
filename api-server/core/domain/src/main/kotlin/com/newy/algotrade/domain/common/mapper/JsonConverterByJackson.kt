package com.newy.algotrade.domain.common.mapper

import com.fasterxml.jackson.databind.InjectableValues
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

class JsonConverterByJackson(private val mapper: ObjectMapper) : JsonConverter {
    override fun toJson(source: Any): String =
        mapper.writeValueAsString(source)

    override fun <T : Any> _toObject(source: String, extraValues: Map<String, Any>, clazz: KClass<T>): T {
        try {
            return when (clazz.java) {
                Unit::class.java -> Unit as T
                String::class.java -> source as T
                else -> mapper
                    .readerFor(clazz.java)
                    .with(if (extraValues.isNotEmpty()) InjectableValues.Std(extraValues) else null)
                    .readValue(source)
            }
        } catch (e: Exception) {
            // TODO logger
            println("JSON")
            println(source)
            throw e
        }
    }
}