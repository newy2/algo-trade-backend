package com.newy.algotrade.coroutine_based_application.price.adpter.out.web

import com.newy.algotrade.coroutine_based_application.price.port.out.LoadProductPricePort
import com.newy.algotrade.coroutine_based_application.price.port.out.model.LoadProductPriceParam
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.extension.ProductPrice

class LoadProductPriceSelector(
    private val components: Map<Market, LoadProductPricePort>
) : LoadProductPricePort {
    override suspend fun productPrices(param: LoadProductPriceParam): List<ProductPrice> {
        return components.getValue(param.market).productPrices(param)
    }
}