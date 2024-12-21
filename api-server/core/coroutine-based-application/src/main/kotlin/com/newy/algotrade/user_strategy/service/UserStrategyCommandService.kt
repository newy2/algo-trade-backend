package com.newy.algotrade.user_strategy.service

import com.newy.algotrade.common.coroutine.EventBus
import com.newy.algotrade.common.event.CreateUserStrategyEvent
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.strategy.port.`in`.HasStrategyQuery
import com.newy.algotrade.user_strategy.port.`in`.UserStrategyUseCase
import com.newy.algotrade.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.user_strategy.port.out.*

open class UserStrategyCommandService(
    private val hasStrategyQuery: HasStrategyQuery,
    private val existsUserStrategyPort: ExistsUserStrategyPort,
    private val saveUserStrategyPort: SaveUserStrategyPort,
    private val findMarketPort: FindMarketPort,
    private val findProductPort: FindProductPort,
    private val saveAllUserStrategyProductPort: SaveAllUserStrategyProductPort,
    private val eventBus: EventBus<CreateUserStrategyEvent>,
) : UserStrategyUseCase {
    constructor(
        hasStrategyQuery: HasStrategyQuery,
        userStrategyPort: UserStrategyPort,
        marketPort: MarketPort,
        productPort: ProductPort,
        saveAllUserStrategyProductPort: SaveAllUserStrategyProductPort,
        eventBus: EventBus<CreateUserStrategyEvent>,
    ) : this(
        hasStrategyQuery = hasStrategyQuery,
        existsUserStrategyPort = userStrategyPort,
        saveUserStrategyPort = userStrategyPort,
        findMarketPort = marketPort,
        findProductPort = productPort,
        saveAllUserStrategyProductPort = saveAllUserStrategyProductPort,
        eventBus = eventBus,
    )

    override suspend fun setUserStrategy(userStrategy: SetUserStrategyCommand): Long {
        val marketIds = findMarketPort.findMarketIdsBy(userStrategy.marketAccountId).also {
            if (it.isEmpty()) {
                throw NotFoundRowException("marketAccountId 를 찾을 수 없습니다.")
            }
        }
        if (!hasStrategyQuery.hasStrategy(userStrategy.strategyClassName)) {
            throw NotFoundRowException("strategyId 를 찾을 수 없습니다.")
        }
        if (existsUserStrategyPort.existsUserStrategy(userStrategy.toDomainEntity().setUserStrategyKey)) {
            throw IllegalArgumentException("이미 등록한 전략입니다.")
        }

        val savedProducts = findProductPort.findProducts(
            marketIds = marketIds,
            productType = userStrategy.productType,
            productCodes = userStrategy.productCodes
        ).also { savedProducts ->
            val savedProductCodes = savedProducts.map { it.code }
            val requestProductCodes = userStrategy.productCodes
            val diff = requestProductCodes.subtract(savedProductCodes.toSet())

            if (diff.isNotEmpty()) {
                throw NotFoundRowException("productCode 를 찾을 수 없습니다. (${diff})")
            }
        }

        val userStrategyId = saveUserStrategyPort.saveUserStrategy(userStrategy.toDomainEntity())

        saveAllUserStrategyProductPort.saveAllUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = savedProducts.map { it.id }
        )

        eventBus.publishEvent(CreateUserStrategyEvent(userStrategyId))

        return userStrategyId
    }
}