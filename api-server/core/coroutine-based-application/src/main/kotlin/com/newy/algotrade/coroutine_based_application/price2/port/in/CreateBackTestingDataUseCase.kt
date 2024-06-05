package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime

interface CreateBackTestingDataUseCase {
    suspend fun createData(
        productPriceKey: ProductPriceKey,
        searchBeginTime: OffsetDateTime,
        searchEndTime: OffsetDateTime,
        seedSize: Int = DEFAULT_CANDLE_SIZE,
    ): List<ProductPrice>
}
