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
    private val hasUserStrategyPort: HasUserStrategyPort,
    private val setUserStrategyPort: SetUserStrategyPort,
    private val getMarketPort: GetMarketPort,
    private val getProductPort: GetProductPort,
    private val setUserStrategyProductPort: SetUserStrategyProductPort,
    private val eventBus: EventBus<CreateUserStrategyEvent>,
) : UserStrategyUseCase {
    constructor(
        hasStrategyQuery: HasStrategyQuery,
        userStrategyPort: UserStrategyPort,
        getMarketPort: GetMarketPort,
        getProductPort: GetProductPort,
        setUserStrategyProductPort: SetUserStrategyProductPort,
        eventBus: EventBus<CreateUserStrategyEvent>,
    ) : this(
        hasStrategyQuery = hasStrategyQuery,
        hasUserStrategyPort = userStrategyPort,
        setUserStrategyPort = userStrategyPort,
        getMarketPort = getMarketPort,
        getProductPort = getProductPort,
        setUserStrategyProductPort = setUserStrategyProductPort,
        eventBus = eventBus,
    )

    override suspend fun setUserStrategy(userStrategy: SetUserStrategyCommand): Long = withContext(Dispatchers.IO) {
        val marketIds = listOf(
            async { getMarketPort.getMarketIdsBy(userStrategy.marketAccountId) },
            async { hasStrategyQuery.hasStrategy(userStrategy.strategyClassName) },
            async { hasUserStrategyPort.hasUserStrategy(userStrategy.toDomainEntity().setUserStrategyKey) }
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

        val savedProducts = getProductPort.getProducts(
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

        val userStrategyId = setUserStrategyPort.setUserStrategy(userStrategy.toDomainEntity())

        setUserStrategyProductPort.setUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = savedProducts.map { it.id }
        )

        eventBus.publishEvent(CreateUserStrategyEvent(userStrategyId))

        return@withContext userStrategyId
    }
}