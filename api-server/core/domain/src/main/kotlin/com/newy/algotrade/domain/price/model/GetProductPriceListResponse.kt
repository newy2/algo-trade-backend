package com.newy.algotrade.domain.price.model

import com.newy.algotrade.domain.common.extension.ProductPrice

interface GetProductPriceListResponse {
    val prices: List<ProductPrice>
}
