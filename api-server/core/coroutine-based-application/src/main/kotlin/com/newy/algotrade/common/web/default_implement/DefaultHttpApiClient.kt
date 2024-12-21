package com.newy.algotrade.common.web.default_implement

import com.newy.algotrade.common.web.http.FormData
import com.newy.algotrade.common.web.http.HttpApiClient
import com.newy.algotrade.domain.common.mapper.JsonConverter
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

class DefaultHttpApiClient(
    private val client: OkHttpClient,
    private val host: String = "",
    private val jsonConverter: JsonConverter,
) : HttpApiClient {
    private suspend fun <T : Any> call(
        method: String,
        path: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        body: RequestBody? = null,
        jsonExtraValues: Map<String, Any> = emptyMap(),
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

        // TODO log
        val result = client.newCall(request).awaitCall().body!!.string()

        return jsonConverter._toObject(result, jsonExtraValues, clazz)
    }

    override suspend fun <T : Any> _get(
        path: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>
    ): T =
        this.call(
            method = "GET",
            path = path,
            headers = headers,
            params = params,
            jsonExtraValues = jsonExtraValues,
            clazz = clazz,
        )

    override suspend fun <T : Any> _post(
        path: String,
        params: Map<String, String>,
        body: Any,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>
    ): T =

        this.call(
            method = "POST",
            path = path,
            headers = headers,
            params = params,
            body = if (body is FormData) {
                // TODO Content-type(application/x-www-form-urlencoded) 에 대한 예외처리
                // TODO 지금은 LS증권 access token 얻을 때만 사용됨. 다른 API 에서 form 데이터를 많이 사용하면 그 때 더 추상화 하자.
                FormBody.Builder().also {
                    body.values.forEach { (key, value) ->
                        it.addEncoded(key, value)
                    }
                }.build()
            } else {
                jsonConverter.toJson(body).toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )
            },
            jsonExtraValues = jsonExtraValues,
            clazz = clazz,
        )
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
