package com.newy.algotrade.coroutine_based_application.product.adapter.out.external_system

import com.newy.algotrade.coroutine_based_application.product.port.out.ProductPriceQueryPort
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.GetProductPriceHttpParam

open class FetchProductPriceProxy(
    private val components: Map<Market, ProductPriceQueryPort>
) : ProductPriceQueryPort {
    override suspend fun getProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> {
        return components.getValue(param.market).getProductPrices(param)
    }
}