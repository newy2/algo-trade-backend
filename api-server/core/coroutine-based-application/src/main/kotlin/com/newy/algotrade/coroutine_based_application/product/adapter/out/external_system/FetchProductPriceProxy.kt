package com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPricePort
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.GetProductPriceHttpParam

open class FetchProductPriceProxy(
    private val components: Map<Market, ProductPricePort>
) : ProductPricePort {
    override suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> {
        return components.getValue(param.market).fetchProductPrices(param)
    }
}