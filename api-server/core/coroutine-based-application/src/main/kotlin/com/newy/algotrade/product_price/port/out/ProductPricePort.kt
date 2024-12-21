package com.newy.algotrade.product_price.port.out

import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.GetProductPriceHttpParam

interface ProductPricePort
    : FetchProductPricesPort

fun interface FetchProductPricesPort {
    suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice>
}