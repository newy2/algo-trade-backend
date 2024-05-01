@file:Suppress("UNCHECKED_CAST")

package com.newy.algotrade.web_flux.common.web

import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import kotlin.reflect.KClass

class HttpApiClientByWebClient(
    private val client: WebClient = WebClient.create()
) : HttpApiClient {
    private suspend fun <T : Any> call(
        method: HttpMethod,
        path: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        body: Any = Unit,
        clazz: KClass<T>,
    ): T {
        return client
            .method(method)
            .uri {
                it.path(path).also {
                    params.forEach { (key, value) ->
                        it.queryParam(key, value)
                    }
                }.build()
            }
            .headers {
                headers.forEach { (key, value) ->
                    it[key] = value
                }
            }
            .also {
                if (body !is Unit) {
                    it.bodyValue(body)
                }
            }
            .retrieve()
            .awaitBody(clazz)
    }

    override suspend fun <T : Any> _get(
        path: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        clazz: KClass<T>
    ): T {
        return this.call(
            method = HttpMethod.GET,
            path = path,
            params = params,
            headers = headers,
            clazz = clazz,
        )
    }

    override suspend fun <T : Any> _post(
        path: String,
        body: Any,
        headers: Map<String, String>,
        clazz: KClass<T>,
    ): T {
        return this.call(
            method = HttpMethod.POST,
            path = path,
            body = body,
            headers = headers,
            clazz = clazz,
        )
    }
}

suspend fun <T : Any> WebClient.ResponseSpec.awaitBody(type: KClass<T>): T =
    when (type.java) {
        Unit::class.java -> awaitBodilessEntity().let { Unit as T }
        else -> bodyToMono(type.java).awaitSingle()
    }