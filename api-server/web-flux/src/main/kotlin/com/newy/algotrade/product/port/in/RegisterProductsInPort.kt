package com.newy.algotrade.product.port.`in`

import com.newy.algotrade.product.port.`in`.model.RegisterProductResult

fun interface RegisterProductsInPort {
    suspend fun registerProducts(userId: Long): RegisterProductResult
}