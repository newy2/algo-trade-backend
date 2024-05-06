package com.newy.algotrade.domain.price.model.jackson.bybit

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.extension.ProductPrice
import java.time.Duration

@JsonIgnoreProperties(ignoreUnknown = true)
class ByBitProductPriceWebSocketResponse(val price: ProductPrice) {
    @JsonCreator
    constructor(
        @JsonProperty("data") node: JsonNode,
    ) : this(
        price = node[0].let {
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