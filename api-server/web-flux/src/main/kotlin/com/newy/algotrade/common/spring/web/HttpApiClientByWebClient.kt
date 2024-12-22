@file:Suppress("UNCHECKED_CAST")

package com.newy.algotrade.common.spring.web

import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.web.http.FormData
import com.newy.algotrade.common.web.http.HttpApiClient
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpMethod
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import kotlin.reflect.KClass

@Deprecated("OkHttp 라이브러리 사용으로 인한 WebClient 미사용 처리")
class HttpApiClientByWebClient(
    private val client: WebClient,
    private val host: String,
    private val jsonConverter: JsonConverter,
) : HttpApiClient {
    private suspend fun <T : Any> call(
        method: HttpMethod,
        path: String,
        headers: Map<String, String> = emptyMap(),
        params: Map<String, String> = emptyMap(),
        body: Any = Unit,
        jsonExtraValues: Map<String, Any> = emptyMap(),
        clazz: KClass<T>,
    ): T {
        return client
            .method(method)
            .uri(host) {
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
                if (body is FormData) {
                    // TODO Content-type(application/x-www-form-urlencoded) 에 대한 예외처리
                    // TODO 지금은 LS증권 access token 얻을 때만 사용됨. 다른 API 에서 form 데이터를 많이 사용하면 그 때 더 추상화 하자.
                    it.body(BodyInserters.fromFormData(LinkedMultiValueMap<String, String>().also {
                        body.values.forEach { (key, value) ->
                            it[key] = value
                        }
                    }))
                } else if (body !is Unit) {
                    it.bodyValue(body)
                }
            }
            .retrieve()
            .awaitBody(jsonConverter, jsonExtraValues, clazz)
    }

    override suspend fun <T : Any> _get(
        path: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>
    ): T {
        return this.call(
            method = HttpMethod.GET,
            path = path,
            params = params,
            headers = headers,
            jsonExtraValues = jsonExtraValues,
            clazz = clazz,
        )
    }

    override suspend fun <T : Any> _post(
        path: String,
        params: Map<String, String>,
        body: Any,
        headers: Map<String, String>,
        jsonExtraValues: Map<String, Any>,
        clazz: KClass<T>
    ): T {
        return this.call(
            method = HttpMethod.POST,
            path = path,
            params = params,
            body = body,
            headers = headers,
            jsonExtraValues = jsonExtraValues,
            clazz = clazz,
        )
    }
}

// TODO JSON 파싱 성능이 문제가 되려나..?
suspend fun <T : Any> WebClient.ResponseSpec.awaitBody(
    jsonConverter: JsonConverter,
    jsonExtraValues: Map<String, Any>,
    clazz: KClass<T>,
): T {
    if (clazz.java == Unit::class.java) {
        return awaitBodilessEntity().let { Unit as T }
    }

    val json = bodyToMono(String::class.java).awaitSingle()
    return jsonConverter._toObject(source = json, extraValues = jsonExtraValues, clazz)
}
//    when (clazz.java) {
//        Unit::class.java -> awaitBodilessEntity().let { Unit as T }
//        else -> bodyToMono(String::class.java)
//            .map { jsonConverter._toObject(source = it, extraValues = jsonExtraValues, clazz) }
//            .awaitSingle()
//    }