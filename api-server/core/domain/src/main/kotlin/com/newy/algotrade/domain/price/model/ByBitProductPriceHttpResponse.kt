package com.newy.algotrade.domain.price.model

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.newy.algotrade.domain.chart.Candle
import java.time.Duration
import java.time.temporal.ChronoUnit

// TODO Candle 이름을 변경할까?
typealias ProductPrice = Candle

@JsonIgnoreProperties(ignoreUnknown = true)
class ByBitProductPriceHttpResponse(override val prices: List<ProductPrice>) : GetProductPriceListResponse {
    companion object {
        fun jsonExtraValues(interval: String) =
            mapOf("interval" to interval)
    }

    @JsonCreator
    constructor(
        @JacksonInject("interval") interval: String,
        @JsonProperty("result") node: JsonNode,
    ) : this(
        prices = node["list"].map {
            // TODO 사용 가능한 interval (1,3,5,15,30,60,120,240,360,720,D,M,W)
            Candle.TimeFrame.from(Duration.of(interval.toLong(), ChronoUnit.MINUTES))!!(
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