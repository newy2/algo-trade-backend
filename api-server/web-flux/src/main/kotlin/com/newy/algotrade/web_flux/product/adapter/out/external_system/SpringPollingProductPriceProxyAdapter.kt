package com.newy.algotrade.web_flux.product.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.common.web.default_implement.DefaultWebSocketClient
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.PollingProductPriceProxy
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.PollingProductPriceWithByBitWebSocket
import com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system.PollingProductPriceWithHttpApi
import com.newy.algotrade.coroutine_based_application.product.port.out.OnReceivePollingPricePort
import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPricePort
import com.newy.algotrade.domain.common.consts.GlobalEnv
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.mapper.JsonConverter
import com.newy.algotrade.web_flux.common.annotation.ExternalSystemAdapter
import okhttp3.OkHttpClient

@ExternalSystemAdapter
class SpringPollingProductPriceProxyAdapter(
    getProductPricePort: ProductPricePort,
    okHttpClient: OkHttpClient,
    jsonConverter: JsonConverter,
    onReceivePollingPricePort: OnReceivePollingPricePort,
    globalEnv: GlobalEnv,
) : PollingProductPriceProxy(
    mapOf(
        Key(Market.LS_SEC, ProductType.SPOT) to PollingProductPriceWithHttpApi(
            loader = getProductPricePort,
            delayMillis = 1000,
        ),
        Key(Market.BY_BIT, ProductType.SPOT) to PollingProductPriceWithByBitWebSocket(
            client = DefaultWebSocketClient(
                client = okHttpClient,
                url = "${globalEnv.BY_BIT_WEB_SOCKET_URL}/v5/public/spot",
            ),
            productType = ProductType.SPOT,
            jsonConverter = jsonConverter,
        )
    ),
    onReceivePollingPricePort
) {
}