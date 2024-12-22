package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.common.consts.GlobalEnv
import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.mapper.JsonConverter
import com.newy.algotrade.common.web.default_implement.DefaultWebSocketClient
import com.newy.algotrade.product_price.port.out.OnReceivePollingPricePort
import com.newy.algotrade.product_price.port.out.ProductPricePort
import com.newy.algotrade.spring.annotation.ExternalSystemAdapter
import okhttp3.OkHttpClient

@ExternalSystemAdapter
class SpringPollingProductPriceProxyAdapter(
    getProductPricePort: ProductPricePort,
    okHttpClient: OkHttpClient,
    jsonConverter: JsonConverter,
    onReceivePollingPricePort: OnReceivePollingPricePort,
    globalEnv: GlobalEnv,
) : PollingProductPriceProxyAdapter(
    mapOf(
        Key(Market.LS_SEC, ProductType.SPOT) to PollingProductPriceWithHttpClient(
            loader = getProductPricePort,
            delayMillis = 1000,
            pollingCallback = { (productPriceKey, productPriceList) ->
                onReceivePollingPricePort.onReceivePrice(productPriceKey, productPriceList)
            }
        ),
        Key(Market.BY_BIT, ProductType.SPOT) to PollingProductPriceWithByBitWebSocket(
            productType = ProductType.SPOT,
            client = DefaultWebSocketClient(
                client = okHttpClient,
                url = "${globalEnv.BY_BIT_WEB_SOCKET_URL}/v5/public/spot",
            ),
            jsonConverter = jsonConverter,
            pollingCallback = { (productPriceKey, productPriceList) ->
                onReceivePollingPricePort.onReceivePrice(productPriceKey, productPriceList)
            }
        ),
    ),
) {
}