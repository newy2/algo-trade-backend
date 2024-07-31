package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey

interface FetchProductPriceQuery {
    suspend fun fetchInitProductPrices(productPriceKey: ProductPriceKey): List<ProductPrice>
    suspend fun requestPollingProductPrice(productPriceKey: ProductPriceKey)
    fun requestUnPollingProductPrice(productPriceKey: ProductPriceKey)
}