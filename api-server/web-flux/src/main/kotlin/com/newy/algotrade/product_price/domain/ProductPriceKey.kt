package com.newy.algotrade.product_price.domain

import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.consts.ProductType
import java.time.Duration

data class ProductPriceKey(
    val market: Market,
    val productType: ProductType,
    val productCode: String,
    val interval: Duration,
)