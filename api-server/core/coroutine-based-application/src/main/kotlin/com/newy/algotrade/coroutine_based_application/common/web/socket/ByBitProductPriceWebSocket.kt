package com.newy.algotrade.coroutine_based_application.common.web.socket

import com.newy.algotrade.coroutine_based_application.common.web.WebSocketClient
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverter
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceWebSocketResponse
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlin.coroutines.CoroutineContext

class ByBitProductPriceWebSocket(
    client: WebSocketClient,
    private val productType: ProductType,
    jsonConverter: JsonConverter,
    coroutineContext: CoroutineContext,
    callback: suspend (Pair<ProductPriceKey, List<ProductPrice>>) -> Unit,
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

    override suspend fun eachProcess(json: String): Pair<ProductPriceKey, List<ProductPrice>>? {
        return try {
            val extraValues = mapOf("productType" to productType.name)
            val dto = jsonConverter.toObject<ByBitProductPriceWebSocketResponse>(json, extraValues)
            return dto.productPriceKey to dto.prices
        } catch (e: Throwable) {
            null
        }
    }
}