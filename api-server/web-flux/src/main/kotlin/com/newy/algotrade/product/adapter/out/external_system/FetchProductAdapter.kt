package com.newy.algotrade.product.adapter.out.external_system

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.web.helper.ApiHelperFactory
import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.out.FetchProductsOutPort
import com.newy.algotrade.spring.annotation.ExternalSystemAdapter

@ExternalSystemAdapter
class FetchProductAdapter(
    private val factory: ApiHelperFactory
) : FetchProductsOutPort {
    override suspend fun fetchProducts(
        marketCode: MarketCode,
        productType: ProductType,
        privateApiInfos: Map<MarketCode, PrivateApiInfo>
    ): RegisterProducts {
        return factory.getInstance(marketCode).getProducts(
            privateApiInfo = privateApiInfos[marketCode],
            productType = productType,
        )
    }
}