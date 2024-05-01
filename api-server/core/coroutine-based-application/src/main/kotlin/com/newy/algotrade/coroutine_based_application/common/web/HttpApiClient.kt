package com.newy.algotrade.coroutine_based_application.common.web

import kotlin.reflect.KClass

interface HttpApiClient {
    suspend fun <T : Any> _get(
        path: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        clazz: KClass<T>,
    ): T

    suspend fun <T : Any> _post(
        path: String,
        body: Any,
        headers: Map<String, String>,
        clazz: KClass<T>,
    ): T
}

suspend inline fun <reified T : Any> HttpApiClient.get(
    path: String,
    params: Map<String, String> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
) = _get(path, params, headers, T::class)

suspend inline fun <reified T : Any> HttpApiClient.post(
    path: String,
    body: Any = Unit,
    headers: Map<String, String> = emptyMap(),
) = _post(path, body, headers, T::class)