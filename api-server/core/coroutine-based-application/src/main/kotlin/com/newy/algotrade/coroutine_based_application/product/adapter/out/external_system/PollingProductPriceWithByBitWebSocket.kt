package com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.common.web.by_bit.ByBitWebSocket
import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketClient
import com.newy.algotrade.coroutine_based_application.product.port.out.PollingProductPricePort
import com.newy.algotrade.domain.common.annotation.ForTesting
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverter
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.price.adapter.out.web.model.jackson.ByBitProductPriceWebSocketResponse
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class PollingProductPriceWithByBitWebSocket(
    client: WebSocketClient,
    private val productType: ProductType,
    jsonConverter: JsonConverter,
    @ForTesting coroutineContext: CoroutineContext = Dispatchers.IO,
) : PollingProductPricePort,
    ByBitWebSocket<ProductPriceKey, List<ProductPrice>>(client, jsonConverter, coroutineContext) {
    override fun topic(key: ProductPriceKey): String {
        val interval = if (key.interval.toDays() >= 1) "D" else key.interval.toMinutes().toString()
        return "kline.$interval.${key.productCode}"
    }

    override suspend fun parsingJson(json: String): Pair<ProductPriceKey, List<ProductPrice>>? {
        return try {
            val extraValues = mapOf("productType" to productType.name)
            val dto = jsonConverter.toObject<ByBitProductPriceWebSocketResponse>(json, extraValues)
            return dto.productPriceKey to dto.prices
        } catch (e: Throwable) {
            null
        }
    }
}