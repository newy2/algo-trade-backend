package com.newy.algotrade.user_strategy.port.out

import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.user_strategy.Product

interface ProductPort
    : FindProductPort

fun interface FindProductPort {
    suspend fun findProducts(
        marketIds: List<Long>,
        productType: ProductType,
        productCodes: List<String>
    ): List<Product>
}