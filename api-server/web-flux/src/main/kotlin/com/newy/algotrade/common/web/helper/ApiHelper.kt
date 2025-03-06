package com.newy.algotrade.common.web.helper

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.web.http.HttpApiRateLimit
import com.newy.algotrade.product.domain.RegisterProducts

abstract class ApiHelper {
    protected val rateLimits = mutableMapOf<String, HttpApiRateLimit>()

    protected suspend fun awaitRateLimit(key: String) {
        rateLimits.getValue(key).await()
    }

    abstract suspend fun isValidPrivateApiInfo(privateApiInfo: PrivateApiInfo): Boolean
    abstract suspend fun getProducts(privateApiInfo: PrivateApiInfo?, productType: ProductType): RegisterProducts
}