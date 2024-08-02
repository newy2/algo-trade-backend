package com.newy.algotrade.coroutine_based_application.user_strategy.service

import com.newy.algotrade.coroutine_based_application.common.coroutine.EventBus
import com.newy.algotrade.coroutine_based_application.common.event.CreateUserStrategyEvent
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.UserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

open class UserStrategyCommandService(
    private val marketPort: GetMarketPort,
    private val strategyPort: HasStrategyPort,
    private val productPort: GetProductPort,
    private val userStrategyPort: UserStrategyPort,
    private val userStrategyProductPort: UserStrategyProductCommandPort,
    private val eventBus: EventBus<CreateUserStrategyEvent>,
) : UserStrategyUseCase {
    override suspend fun setUserStrategy(userStrategy: SetUserStrategyCommand): Boolean = withContext(Dispatchers.IO) {
        val marketIds = listOf(
            async { marketPort.getMarketIdsBy(userStrategy.marketAccountId) },
            async { strategyPort.hasStrategyByClassName(userStrategy.strategyClassName) },
            async {
                userStrategyPort.hasUserStrategy(
                    marketServerAccountId = userStrategy.marketAccountId,
                    strategyClassName = userStrategy.strategyClassName,
                    productType = userStrategy.productType,
                )
            }
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

        val savedProducts = productPort.getProducts(
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

        val userStrategyId = userStrategyPort.setUserStrategy(
            marketServerAccountId = userStrategy.marketAccountId,
            strategyClassName = userStrategy.strategyClassName,
            productType = userStrategy.productType,
            productCategory = userStrategy.productCategory,
            timeFrame = userStrategy.timeFrame
        )

        userStrategyProductPort.setUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = savedProducts.map { it.id }
        )

        eventBus.publishEvent(CreateUserStrategyEvent(userStrategyId))

        return@withContext true
    }
}