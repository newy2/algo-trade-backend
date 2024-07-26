package com.newy.algotrade.coroutine_based_application.product.port.out

import com.newy.algotrade.coroutine_based_application.product.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice

interface ProductPriceQueryPort {
    suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice>
}