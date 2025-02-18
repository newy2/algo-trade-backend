package com.newy.algotrade.product.port.out

import com.newy.algotrade.product.domain.RegisterProducts

fun interface DeleteProductsOutPort {
    suspend fun deleteProducts(products: RegisterProducts): Int
}