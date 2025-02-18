package com.newy.algotrade.product.domain

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType

data class RegisterProduct(
    val marketCode: MarketCode,
    val type: ProductType,
    val code: String,
    val name: String
)