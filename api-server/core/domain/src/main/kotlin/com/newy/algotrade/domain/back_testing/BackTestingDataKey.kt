package com.newy.algotrade.domain.back_testing

import com.newy.algotrade.domain.product.ProductPriceKey
import java.time.OffsetDateTime

data class BackTestingDataKey(
    val productPriceKey: ProductPriceKey,
    val searchBeginTime: OffsetDateTime,
    val searchEndTime: OffsetDateTime,
)