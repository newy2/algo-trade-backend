package com.newy.algotrade.coroutine_based_application.common.web.socket

import com.newy.algotrade.coroutine_based_application.common.web.WebSocketClient
import com.newy.algotrade.coroutine_based_application.price.domain.model.ProductPriceKey
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverter
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceWebSocketResponse
import kotlin.coroutines.CoroutineContext

class ByBitProductPriceWebSocket(
    client: WebSocketClient,
    jsonConverter: JsonConverter,
    coroutineContext: CoroutineContext,
    callback: suspend (List<ProductPrice>) -> Unit,
) : ByBitWebSocket<ProductPriceKey, List<ProductPrice>>(
    client,
    jsonConverter,
    coroutineContext,
    callback,
) {
    override fun parsing(data: ProductPriceKey): String {
        val interval = if (data.interval.toDays() >= 1) "D" else data.interval.toMinutes().toString()
        return "kline.$interval.${data.productCode}"
    }

    override suspend fun eachProcess(message: String): List<ProductPrice>? {
        return try {
            jsonConverter.toObject<ByBitProductPriceWebSocketResponse>(message).prices
        } catch (e: Throwable) {
            null
        }
    }
}