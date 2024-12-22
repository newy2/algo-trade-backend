package com.newy.algotrade.product_price.adapter.out.external_system

import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.domain.extension.ProductPrice
import com.newy.algotrade.product_price.domain.GetProductPriceHttpParam
import com.newy.algotrade.product_price.port.out.ProductPricePort

open class FetchProductPriceProxyAdapter(
    private val components: Map<Market, ProductPricePort>
) : ProductPricePort {
    override suspend fun fetchProductPrices(param: GetProductPriceHttpParam): List<ProductPrice> {
        return components.getValue(param.market).fetchProductPrices(param)
    }
}