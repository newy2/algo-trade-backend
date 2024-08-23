package com.newy.algotrade.coroutine_based_application.product_price.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.common.web.by_bit.ByBitWebSocket
import com.newy.algotrade.coroutine_based_application.common.web.socket.WebSocketClient
import com.newy.algotrade.domain.common.annotation.ForTesting
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.common.mapper.JsonConverter
import com.newy.algotrade.domain.common.mapper.toObject
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.product_price.jackson.ByBitProductPriceWebSocketResponse
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