package com.newy.algotrade.user_strategy.port.out

import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.user_strategy.domain.Product

interface ProductPort
    : FindProductPort

fun interface FindProductPort {
    suspend fun findProducts(
        marketIds: List<Long>,
        productType: ProductType,
        productCodes: List<String>
    ): List<Product>
}