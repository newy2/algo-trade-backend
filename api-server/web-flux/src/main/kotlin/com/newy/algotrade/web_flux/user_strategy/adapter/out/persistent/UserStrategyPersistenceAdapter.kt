package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyPort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.StrategyRepository
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyEntity
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyRepository
import org.springframework.stereotype.Component

@Component
class UserStrategyPersistenceAdapter(
    private val strategyRepository: StrategyRepository,
    private val userStrategyRepository: UserStrategyRepository,
) : UserStrategyPort {
    override suspend fun setUserStrategy(
        marketServerAccountId: Long,
        strategyClassName: String,
        productType: ProductType,
        productCategory: ProductCategory,
        timeFrame: Candle.TimeFrame,
    ): Long =
        userStrategyRepository.save(
            UserStrategyEntity(
                marketAccountId = marketServerAccountId,
                strategyId = strategyRepository.findByClassName(strategyClassName)!!,
                productType = productType.name,
                productCategory = productCategory.name,
                timeFrame = timeFrame.name,
            )
        ).id

    override suspend fun hasUserStrategy(
        marketServerAccountId: Long,
        strategyClassName: String,
        productType: ProductType
    ): Boolean {
        return userStrategyRepository.existsByMarketAccountIdAndStrategyIdAndProductType(
            marketAccountId = marketServerAccountId,
            strategyId = strategyRepository.findByClassName(strategyClassName)!!,
            productType = productType.name,
        )
    }
}