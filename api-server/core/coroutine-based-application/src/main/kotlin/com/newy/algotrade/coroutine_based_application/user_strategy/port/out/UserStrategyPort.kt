package com.newy.algotrade.coroutine_based_application.user_strategy.port.out

import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType

interface UserStrategyPort : SetUserStrategyPort, HasUserStrategyPort

interface SetUserStrategyPort {
    suspend fun setUserStrategy(
        marketServerAccountId: Long,
        strategyId: Long,
        productType: ProductType,
        productCategory: ProductCategory,
    ): Long
}

interface HasUserStrategyPort {
    suspend fun hasUserStrategy(
        marketServerAccountId: Long,
        strategyId: Long,
        productType: ProductType,
    ): Boolean
}