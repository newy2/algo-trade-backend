package com.newy.algotrade.coroutine_based_application.user_strategy.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.strategy.port.`in`.HasStrategyQuery
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

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

    override suspend fun setUserStrategy(userStrategy: SetUserStrategyCommand): Long = withContext(Dispatchers.IO) {
        val marketIds = listOf(
            async { findMarketPort.findMarketIdsBy(userStrategy.marketAccountId) },
            async { hasStrategyQuery.hasStrategy(userStrategy.strategyClassName) },
            async { existsUserStrategyPort.existsUserStrategy(userStrategy.toDomainEntity().setUserStrategyKey) }
        ).awaitAll().let {
            val (marketIds, hasStrategy, hasUserStrategy) = it
            if ((marketIds as List<Long>).isEmpty()) {
                throw NotFoundRowException("marketAccountId 를 찾을 수 없습니다.")
            }
            if (!(hasStrategy as Boolean)) {
                throw NotFoundRowException("strategyId 를 찾을 수 없습니다.")
            }
            if (hasUserStrategy as Boolean) {
                throw IllegalArgumentException("이미 등록한 전략입니다.")
            }

            marketIds
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

        return@withContext userStrategyId
    }
}