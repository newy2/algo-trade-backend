package com.newy.algotrade.domain.product.jackson

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.ProductPriceKey
import java.time.Duration

@JsonIgnoreProperties(ignoreUnknown = true)
class ByBitProductPriceWebSocketResponse(
    val productPriceKey: ProductPriceKey,
    val prices: List<ProductPrice>,
) {
    @JsonCreator
    constructor(
        @JacksonInject("productType") productType: String,
        @JsonProperty("topic") topic: String,
        @JsonProperty("data") node: JsonNode,
    ) : this(
        productPriceKey = topic.split(".").let { (_, duration, productCode) ->
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.valueOf(productType),
                productCode,
                if (duration == "D") Duration.ofDays(1) else Duration.ofMinutes(duration.toLong())
            )
        },
        prices = node.map {
            // TODO 사용 가능한 interval (1,3,5,15,30,60,120,240,360,720,D,M,W)
            // TODO 실제로는 좀 줄이자 (1,3,4,15,30,60,D)
            Candle.TimeFrame.from(Duration.ofMinutes(it["interval"].asLong()))!!(
                beginTime = it["start"].asLong(),
                openPrice = it["open"].asDouble().toBigDecimal(),
                highPrice = it["high"].asDouble().toBigDecimal(),
                lowPrice = it["low"].asDouble().toBigDecimal(),
                closePrice = it["close"].asDouble().toBigDecimal(),
                volume = it["volume"].asDouble().toBigDecimal()
            )
        }
    )
}