package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyQueryPort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.product.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyWithProductR2dbcModel
import com.newy.algotrade.web_flux.user_strategy.adapter.out.persistent.repository.UserStrategyWithProductRepository
import kotlinx.coroutines.flow.toList

@PersistenceAdapter
class GetUserStrategyPersistenceAdapter(
    private val repository: UserStrategyWithProductRepository
) : UserStrategyQueryPort {
    override suspend fun getAllUserStrategies(): List<UserStrategyKey> =
        repository
            .findAllWithProducts()
            .toList()
            .toDomainModels()

    override suspend fun getUserStrategy(userStrategyId: Long): UserStrategyKey? =
        repository
            .findWithProducts(userStrategyId)
            .toList()
            .toDomainModels()
            .firstOrNull()
}

fun List<UserStrategyWithProductR2dbcModel>.toDomainModels() =
    this.groupBy { it.id }
        .flatMap { (id, values) ->
            values.map {
                UserStrategyKey(
                    userStrategyId = id.toString(),
                    strategyClassName = it.strategyClassName,
                    productPriceKey = ProductPriceKey(
                        market = Market.valueOf(it.marketCode),
                        productType = ProductType.valueOf(it.productType),
                        productCode = it.productCode,
                        interval = Candle.TimeFrame.valueOf(it.timeFrame).timePeriod,
                    ),
                )
            }
        }