package com.newy.algotrade.product_price.domain

import com.newy.algotrade.common.domain.consts.Market
import com.newy.algotrade.common.domain.consts.ProductType
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class GetProductPriceHttpParam(
    val productPriceKey: ProductPriceKey,
    private val endTime: OffsetDateTime,
    val limit: Int,
) {
    val market = productPriceKey.market
    val productCode = productPriceKey.productCode
    val intervalMinutes = productPriceKey.interval.toMinutes()

    private fun useOnly(market: Market) {
        assert(this.market == market)
    }

    fun endTime(): String {
        return when (market) {
            Market.BY_BIT -> endTime.toInstant().toEpochMilli().toString()
            Market.LS_SEC -> endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            else -> throw NotImplementedError()
        }
    }

    fun category(): String {
        useOnly(Market.BY_BIT)

        return when (productPriceKey.productType) {
            ProductType.SPOT -> productPriceKey.productType.name.lowercase()
            ProductType.PERPETUAL_FUTURE -> "linear"
            else -> throw NotImplementedError()
        }
    }

    fun formattedInterval(): String {
        return if (isIntervalByDays()) "D" else intervalMinutes.toString()
    }

    fun isIntervalByDays(): Boolean {
        return productPriceKey.interval.toDays() >= 1
    }

    fun extraBody(): Map<String, Any> {
        useOnly(Market.LS_SEC)

        return if (isIntervalByDays()) {
            mapOf("gubun" to "2")
        } else {
            mapOf("ncnt" to intervalMinutes)
        }
    }
}