package com.newy.algotrade.coroutine_based_application.price.port.out.model

import com.newy.algotrade.domain.common.consts.EBestTrCode
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

open class LoadProductPriceParam(
    val market: Market,
    private val productType: ProductType,
    val productCode: String,
    private val interval: Duration,
    private val endTime: OffsetDateTime,
    val limit: Int
) {
    val intervalMinutes = interval.toMinutes()

    private fun useOnly(market: Market) {
        assert(this.market == market)
    }

    open fun endTime(): String {
        return when (market) {
            Market.BY_BIT -> endTime.toInstant().toEpochMilli().toString()
            Market.E_BEST -> endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            else -> throw NotImplementedError()
        }
    }

    fun category(): String {
        useOnly(Market.BY_BIT)

        return when (productType) {
            ProductType.SPOT -> productType.name.lowercase()
            ProductType.PERPETUAL_FUTURE -> "linear"
            else -> throw NotImplementedError()
        }
    }

    private fun isIntervalByDays(): Boolean {
        useOnly(Market.E_BEST)

        return interval.toDays() >= 1
    }

    fun trCode(): String {
        useOnly(Market.E_BEST)

        return if (isIntervalByDays()) {
            EBestTrCode.GET_PRODUCT_PRICE_BY_DAY.code
        } else {
            EBestTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code
        }
    }

    fun extraBody(): Map<String, Any> {
        useOnly(Market.E_BEST)

        return if (isIntervalByDays()) {
            mapOf("gubun" to "2")
        } else {
            mapOf("ncnt" to intervalMinutes)
        }
    }
}