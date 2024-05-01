package com.newy.algotrade.integration.common.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.newy.algotrade.coroutine_based_application.common.web.HttpApiClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.closeQuietly
import java.io.IOException
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KClass

class HttpApiClientByOkHttp(
    private val client: OkHttpClient,
    private val host: String = "",
    private val objectMapper: ObjectMapper,
) : HttpApiClient {
    private suspend fun <T : Any> call(
        method: String,
        path: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        body: RequestBody? = null,
        clazz: KClass<T>,
    ): T {
        val request = Request.Builder()
            .method(method, body)
            .also {
                headers.forEach { (key, value) ->
                    it.header(key, value)
                }
            }
            .url(
                host.toHttpUrl()
                    .newBuilder()
                    .encodedPath(path)
                    .also {
                        params.forEach { (key, value) ->
                            it.addQueryParameter(key, value)
                        }
                    }
                    .build()
            )
            .build()

        val result = client.newCall(request).awaitCall().body!!.string()

        return objectMapper.readValue(result, clazz)
    }

    override suspend fun <T : Any> _get(
        path: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        clazz: KClass<T>
    ): T =
        this.call(
            method = "GET",
            path = path,
            headers = headers,
            params = params,
            body = null,
            clazz = clazz,
        )

    override suspend fun <T : Any> _post(
        path: String,
        body: Any,
        headers:
        Map<String, String>,
        clazz: KClass<T>,
    ): T =
        this.call(
            method = "POST",
            path = path,
            headers = headers,
            body = objectMapper.writeValueAsString(body).toRequestBody(
                "application/json; charset=utf-8".toMediaType()
            ),
            clazz = clazz,
        )
}

fun <T : Any> ObjectMapper.readValue(content: String, type: KClass<T>): T =
    when (type.java) {
        Unit::class.java -> Unit as T
        String::class.java -> content as T
        else -> this.readValue(content, type.java)
    }

@ExperimentalCoroutinesApi // resume with a resource cleanup.
suspend fun Call.awaitCall(): Response =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            this.cancel()
        }
        this.enqueue(
            object : Callback {
                override fun onFailure(
                    call: Call,
                    e: IOException,
                ) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(
                    call: Call,
                    response: Response,
                ) {
                    continuation.resume(response) {
                        response.closeQuietly()
                    }
                }
            },
        )
    }
