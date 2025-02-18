package com.newy.algotrade.product.port.`in`

import com.newy.algotrade.product.domain.RegisterProducts

fun interface GetRegisterProductsInPort {
    suspend fun getProducts(userId: Long): RegisterProducts
}