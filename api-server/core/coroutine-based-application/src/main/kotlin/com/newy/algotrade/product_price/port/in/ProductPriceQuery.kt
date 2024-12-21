package com.newy.algotrade.product_price.port.`in`

import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.ProductPriceKey

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