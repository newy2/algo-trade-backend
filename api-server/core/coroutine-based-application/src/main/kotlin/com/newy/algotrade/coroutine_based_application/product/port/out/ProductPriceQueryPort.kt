package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.GetProductPriceHttpParam

interface ProductPriceQueryPort {
    suspend fun getProductPrices(param: GetProductPriceHttpParam): List<ProductPrice>
}