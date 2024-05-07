package com.newy.algotrade.coroutine_based_application.price.port.out

import com.newy.algotrade.coroutine_based_application.price.port.out.model.LoadProductPriceParam
import com.newy.algotrade.domain.common.extension.ProductPrice

interface LoadProductPricePort {
    suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice>
}