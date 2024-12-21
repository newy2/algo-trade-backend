package com.newy.algotrade.back_testing.domain

import com.newy.algotrade.product_price.domain.ProductPriceKey
import java.time.OffsetDateTime

data class BackTestingDataKey(
    val productPriceKey: ProductPriceKey,
    val searchBeginTime: OffsetDateTime,
    val searchEndTime: OffsetDateTime,
)