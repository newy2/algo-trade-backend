package com.newy.algotrade.domain.common.mapper

import kotlin.reflect.KClass

interface JsonConverter {
    fun toJson(source: Any): String
    fun <T : Any> _toObject(source: String, type: KClass<T>): T
}

inline fun <reified T : Any> JsonConverter.toObject(source: String): T =
    this._toObject(source, T::class)