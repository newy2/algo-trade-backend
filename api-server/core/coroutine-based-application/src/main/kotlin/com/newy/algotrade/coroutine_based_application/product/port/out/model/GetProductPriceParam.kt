package com.newy.algotrade.coroutine_based_application.product.port.out.model

import com.newy.algotrade.domain.common.consts.EBestTrCode
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class GetProductPriceParam(
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
            Market.E_BEST -> endTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
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

    fun trCode(): String {
        // TODO Remove this
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