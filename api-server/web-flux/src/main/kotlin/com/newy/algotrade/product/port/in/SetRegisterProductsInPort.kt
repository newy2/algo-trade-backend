package com.newy.algotrade.product.port.`in`

import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult

fun interface SetRegisterProductsInPort {
    suspend fun setProducts(fetchedProducts: RegisterProducts): RegisterProductResult
}