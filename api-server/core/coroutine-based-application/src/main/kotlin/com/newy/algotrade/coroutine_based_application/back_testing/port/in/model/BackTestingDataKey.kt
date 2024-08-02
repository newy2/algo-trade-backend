package com.newy.algotrade.coroutine_based_application.back_testing.port.`in`.model

import com.newy.algotrade.domain.product.ProductPriceKey
import java.time.OffsetDateTime

data class BackTestingDataKey(
    val productPriceKey: ProductPriceKey,
    val searchBeginTime: OffsetDateTime,
    val searchEndTime: OffsetDateTime,
)