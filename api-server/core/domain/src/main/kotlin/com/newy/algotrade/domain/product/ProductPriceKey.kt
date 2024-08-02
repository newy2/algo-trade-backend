package com.newy.algotrade.domain.product

import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import java.time.Duration

data class ProductPriceKey(
    val market: Market,
    val productType: ProductType,
    val productCode: String,
    val interval: Duration,
)