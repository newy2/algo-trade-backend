package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.GetProductPriceHttpParam

interface ProductPricePort
    : FetchProductPricesPort

fun interface FetchProductPricesPort {
    suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice>
}