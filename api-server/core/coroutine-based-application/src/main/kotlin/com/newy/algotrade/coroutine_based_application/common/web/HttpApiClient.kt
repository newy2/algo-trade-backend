package com.newy.algotrade.coroutine_based_application.common.web

import kotlin.reflect.KClass

interface HttpApiClient {
    suspend fun <T : Any> _get(
        path: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>,
    ): T

    suspend fun <T : Any> _post(
        path: String,
        params: Map<String, String>,
        body: Any,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>,
    ): T
}

suspend inline fun <reified T : Any> HttpApiClient.get(
    path: String,
    params: Map<String, String> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
    jsonExtraValues: Map<String, Any> = emptyMap(),
) = _get(path, params, headers, jsonExtraValues, T::class)

suspend inline fun <reified T : Any> HttpApiClient.post(
    path: String,
    params: Map<String, String> = emptyMap(),
    body: Any = Unit,
    headers: Map<String, String> = emptyMap(),
    jsonExtraValues: Map<String, Any> = emptyMap(),
) = _post(path, params, body, headers, jsonExtraValues, T::class)

class FormData(val values: Map<String, String>) {
    constructor(vararg values: Pair<String, String>) : this(values.toMap())
}