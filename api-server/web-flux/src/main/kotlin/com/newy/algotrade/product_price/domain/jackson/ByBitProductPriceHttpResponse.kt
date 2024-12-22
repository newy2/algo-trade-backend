package com.newy.algotrade.product_price.domain.jackson

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.common.extension.ProductPrice
import java.time.Duration

@JsonIgnoreProperties(ignoreUnknown = true)
class ByBitProductPriceHttpResponse(val prices: List<ProductPrice>) {
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