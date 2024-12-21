package com.newy.algotrade.common.domain.mapper

import kotlin.reflect.KClass

interface JsonConverter {
    fun toJson(source: Any): String
    fun <T : Any> _toObject(source: String, extraValues: Map<String, Any>, clazz: KClass<T>): T
}

inline fun <reified T : Any> JsonConverter.toObject(source: String, extraValues: Map<String, Any> = emptyMap()): T =
    this._toObject(source, extraValues, T::class)