package com.newy.algotrade.domain.product.jackson

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.LsSecTrCode
import com.newy.algotrade.domain.common.extension.ProductPrice
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = LsSecProductPriceDeserializer::class)
data class LsSecProductPriceHttpResponse(val prices: List<ProductPrice>) {
    companion object {
        fun jsonExtraValues(code: String, interval: Long) =
            mapOf(
                "code" to code,
                "interval" to interval
            )
    }
}

class LsSecProductPriceDeserializer : StdDeserializer<LsSecProductPriceHttpResponse> {
    constructor() : this(null)
    constructor(vc: Class<*>?) : super(vc)

    override fun deserialize(parser: JsonParser, context: DeserializationContext): LsSecProductPriceHttpResponse {
        val code = context.findInjectableValue("code", null, null) as String
        val interval = context.findInjectableValue("interval", null, null) as Long

        val root = parser.codec.readTree<JsonNode>(parser)
        val list = root["${code}OutBlock1"]

        val formatter = DateTimeFormatter
            .ofPattern("yyyyMMdd HHmmss")
            .withZone(ZoneOffset.ofHours(9))

        return LsSecProductPriceHttpResponse(
            list.map {
                Candle.TimeFrame.from(Duration.ofMinutes(interval))!!(
                    beginTime = beginTime(it, interval, code, formatter),
                    openPrice = it["open"].asDouble().toBigDecimal(),
                    highPrice = it["high"].asDouble().toBigDecimal(),
                    lowPrice = it["low"].asDouble().toBigDecimal(),
                    closePrice = it["close"].asDouble().toBigDecimal(),
                    volume = it["jdiff_vol"].asDouble().toBigDecimal(),
                )
            }
        )
    }

    private fun beginTime(
        item: JsonNode,
        interval: Long,
        code: String,
        formatter: DateTimeFormatter,
    ): OffsetDateTime {
        val date = item["date"].asText()

        val (time, adjustMinutes) =
            if (code == LsSecTrCode.GET_PRODUCT_PRICE_BY_DAY.code) {
                /***
                 * TODO 일봉 데이터에서 시간값이 필요하다면, `metadata["e_time"].asText()` 사용을 고려할 것
                 * 현재, 캔들 데이터의 시간 간격이 동일하다는 가정을 하고 있음.
                 * e_time 사용 시, 수능일 처럼 주식 시장 운영시간이 변동 케이스에 대한 테스트 필요
                 */
                val startTime = "000000"
                Pair(startTime, 0.toLong())
            } else {
                val endTime = item["time"].asText()
                Pair(endTime, interval)
            }

        return OffsetDateTime
            .parse("$date $time", formatter)
            .minusMinutes(adjustMinutes)
    }
}