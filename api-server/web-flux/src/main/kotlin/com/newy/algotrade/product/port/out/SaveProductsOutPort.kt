package com.newy.algotrade.product.port.out

import com.newy.algotrade.product.domain.RegisterProducts

fun interface SaveProductsOutPort {
    suspend fun saveProducts(products: RegisterProducts): Int
}
