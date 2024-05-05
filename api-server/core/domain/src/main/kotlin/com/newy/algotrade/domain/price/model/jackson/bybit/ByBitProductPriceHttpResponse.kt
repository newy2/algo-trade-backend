package com.newy.algotrade.domain.price.model.jackson.bybit

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.model.GetProductPriceListResponse
import java.time.Duration

@JsonIgnoreProperties(ignoreUnknown = true)
class ByBitProductPriceHttpResponse(override val prices: List<ProductPrice>) : GetProductPriceListResponse {
    companion object {
        fun jsonExtraValues(interval: Long) =
            mapOf("interval" to interval)
    }

    @JsonCreator
    constructor(
        @JacksonInject("interval") interval: Long,
        @JsonProperty("result") node: JsonNode,
    ) : this(
        prices = node["list"].map {
            // TODO 사용 가능한 interval (1,3,5,15,30,60,120,240,360,720,D,M,W)
            // TODO 실제로는 좀 줄이자 (1,3,4,15,30,60,D)
            Candle.TimeFrame.from(Duration.ofMinutes(interval))!!(
                beginTime = it[0].asLong(),
                openPrice = it[1].asDouble().toBigDecimal(),
                highPrice = it[2].asDouble().toBigDecimal(),
                lowPrice = it[3].asDouble().toBigDecimal(),
                closePrice = it[4].asDouble().toBigDecimal(),
                volume = it[5].asDouble().toBigDecimal()
            )
        }
    )
}