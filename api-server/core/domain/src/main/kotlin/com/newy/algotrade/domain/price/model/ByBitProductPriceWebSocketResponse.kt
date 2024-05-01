package com.newy.algotrade.domain.price.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.newy.algotrade.domain.chart.Candle
import java.time.Duration
import java.time.temporal.ChronoUnit

@JsonIgnoreProperties(ignoreUnknown = true)
class ByBitProductPriceWebSocketResponse(override val price: ProductPrice) : GetProductPriceResponse {
    @JsonCreator
    constructor(
        @JsonProperty("data") node: JsonNode,
    ) : this(
        price = node[0].let {
            // TODO 사용 가능한 interval (1,3,5,15,30,60,120,240,360,720,D,M,W)
            Candle.TimeFrame.from(Duration.of(it["interval"].asLong(), ChronoUnit.MINUTES))!!(
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