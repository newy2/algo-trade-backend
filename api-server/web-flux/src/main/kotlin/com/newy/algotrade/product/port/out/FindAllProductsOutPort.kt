package com.newy.algotrade.product.port.out

import com.newy.algotrade.product.domain.RegisterProducts

fun interface FindAllProductsOutPort {
    suspend fun findAllProducts(): RegisterProducts
}