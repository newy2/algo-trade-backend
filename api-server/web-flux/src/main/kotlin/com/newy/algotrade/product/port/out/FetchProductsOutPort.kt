package com.newy.algotrade.product.port.out

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProducts

fun interface FetchProductsOutPort {
    suspend fun fetchProducts(
        marketCode: MarketCode,
        productType: ProductType,
        privateApiInfos: Map<MarketCode, PrivateApiInfo>,
    ): RegisterProducts
}