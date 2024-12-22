package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.common.annotation.ForTesting
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.coroutine.PollingCallback
import com.newy.algotrade.common.extension.ProductPrice
import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.mapper.toObject
import com.newy.algotrade.common.web.by_bit.ByBitWebSocket
import com.newy.algotrade.common.web.socket.WebSocketClient
import com.newy.algotrade.product_price.domain.ProductPriceKey
import com.newy.algotrade.product_price.domain.jackson.ByBitProductPriceWebSocketResponse
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class PollingProductPriceWithByBitWebSocket(
    private val productType: ProductType,
    client: WebSocketClient,
    jsonConverter: JsonConverter,
    @ForTesting coroutineContext: CoroutineContext = Dispatchers.IO,
    pollingCallback: PollingCallback<ProductPriceKey, List<ProductPrice>>,
) : ByBitWebSocket<ProductPriceKey, List<ProductPrice>>(client, jsonConverter, coroutineContext, pollingCallback) {
    override suspend fun parsingJson(message: String): Pair<ProductPriceKey, List<ProductPrice>>? {
        return try {
            jsonConverter.toObject<ByBitProductPriceWebSocketResponse>(
                source = message,
                extraValues = ByBitProductPriceWebSocketResponse.jsonExtraValues(productType)
            ).let {
                Pair(it.productPriceKey, it.prices)
            }
        } catch (e: Throwable) {
            null
        }
    }

    override fun topic(key: ProductPriceKey): String {
        val interval = if (key.interval.toDays() >= 1) "D" else key.interval.toMinutes().toString()
        return "kline.$interval.${key.productCode}"
    }
}