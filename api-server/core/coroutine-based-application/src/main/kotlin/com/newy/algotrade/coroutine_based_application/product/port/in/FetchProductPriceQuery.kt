package com.newy.algotrade.coroutine_based_application.product.port.`in`

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.ProductPriceKey

interface FetchProductPriceQuery :
    FetchInitProductPriceQuery,
    RequestPollingProductPriceQuery,
    RequestUnPollingProductPriceQuery

fun interface FetchInitProductPriceQuery {
    suspend fun fetchInitProductPrices(productPriceKey: ProductPriceKey): List<ProductPrice>
}

fun interface RequestPollingProductPriceQuery {
    fun requestPollingProductPrice(productPriceKey: ProductPriceKey)
}

fun interface RequestUnPollingProductPriceQuery {
    fun requestUnPollingProductPrice(productPriceKey: ProductPriceKey)
}