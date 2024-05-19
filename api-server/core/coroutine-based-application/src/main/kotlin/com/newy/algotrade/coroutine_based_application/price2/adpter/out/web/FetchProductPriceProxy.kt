package com.newy.algotrade.coroutine_based_application.price2.adpter.out.web

import com.newy.algotrade.coroutine_based_application.price2.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.extension.ProductPrice

class FetchProductPriceProxy(
    private val components: Map<Market, GetProductPricePort>
) : GetProductPricePort {
    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> {
        return components.getValue(param.market).getProductPrices(param)
    }
}