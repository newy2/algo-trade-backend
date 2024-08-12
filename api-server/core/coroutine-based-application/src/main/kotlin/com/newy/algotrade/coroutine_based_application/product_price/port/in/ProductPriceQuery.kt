package com.newy.algotrade.coroutine_based_application.product_price.port.`in`

import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product_price.ProductPriceKey

interface ProductPriceQuery :
    GetInitProductPriceQuery,
    RequestPollingProductPriceQuery,
    RequestUnPollingProductPriceQuery

fun interface GetInitProductPriceQuery {
    suspend fun getInitProductPrices(productPriceKey: ProductPriceKey): List<ProductPrice>
}

fun interface RequestPollingProductPriceQuery {
    fun requestPollingProductPrice(productPriceKey: ProductPriceKey)
}

fun interface RequestUnPollingProductPriceQuery {
    fun requestUnPollingProductPrice(productPriceKey: ProductPriceKey)
}