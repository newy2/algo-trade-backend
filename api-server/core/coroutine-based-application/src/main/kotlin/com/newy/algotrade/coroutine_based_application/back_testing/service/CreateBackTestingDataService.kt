package com.newy.algotrade.coroutine_based_application.back_testing.service

import com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.CreateBackTestingDataUseCase
import com.newy.algotrade.coroutine_based_application.product_price.port.out.ProductPricePort
import com.newy.algotrade.domain.back_testing.BackTestingDataKey
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.GetProductPriceHttpParam
import com.newy.algotrade.domain.product_price.ProductPriceKey
import java.time.OffsetDateTime
import java.util.*

class CreateBackTestingDataService(
    private val productPricePort: ProductPricePort,
) : CreateBackTestingDataUseCase {
    override suspend fun createData(key: BackTestingDataKey, seedSize: Int): List<ProductPrice> {
        val seedList = createSeedList(key.productPriceKey, key.searchBeginTime, seedSize)
        val backTestingList = createBackTestingList(key.productPriceKey, key.searchBeginTime, key.searchEndTime)
        return seedList + backTestingList
    }

    private suspend fun createSeedList(
        productPriceKey: ProductPriceKey,
        searchBeginTime: OffsetDateTime,
        seedSize: Int,
    ): List<ProductPrice> {
        if (seedSize == 0) {
            return emptyList()
        }

        val results = LinkedList<ProductPrice>()
        while (seedSize > results.size) {
            fetchProductList(results, productPriceKey, searchBeginTime)
        }

        return results.drop(results.size - seedSize)
    }

    private suspend fun createBackTestingList(
        productPriceKey: ProductPriceKey,
        searchBeginTime: OffsetDateTime,
        searchEndTime: OffsetDateTime,
    ): List<ProductPrice> {
        val results = LinkedList<ProductPrice>()
        do {
            fetchProductList(results, productPriceKey, searchEndTime)
        } while (searchBeginTime.isBefore(results.first().time.begin))

        return results.dropWhile { it.time.begin.isBefore(searchBeginTime) }
    }

    private suspend fun fetchProductList(
        results: MutableList<ProductPrice>,
        productPriceKey: ProductPriceKey,
        defaultEndTime: OffsetDateTime
    ) {
        val endTime = results.getOrNull(0)?.time?.begin ?: defaultEndTime
        val maxSize = if (productPriceKey.market == Market.BY_BIT) 1000 else 500

        val ascendingList = productPricePort
            .fetchProductPrices(GetProductPriceHttpParam(productPriceKey, endTime, maxSize))
            .filter { it.time.begin.isBefore(endTime) }

        results.addAll(0, ascendingList)
    }
}