package com.newy.algotrade.coroutine_based_application.user_strategy.application.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.SetUserStrategyUseCase
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

open class SetUserStrategyService(
    private val marketPort: GetMarketPort,
    private val strategyPort: HasStrategyPort,
    private val productPort: GetProductPort,
    private val userStrategyPort: UserStrategyPort,
    private val userStrategyProductPort: SetUserStrategyProductPort,
) : SetUserStrategyUseCase {
    override suspend fun setUserStrategy(userStrategy: SetUserStrategyCommand): Boolean = withContext(Dispatchers.IO) {
        val (marketIds, hasStrategy, hasUserStrategy) = listOf(
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
            Triple(
                it[0] as List<Long>,
                it[1] as Boolean,
                it[2] as Boolean,
            )
        }

        if (marketIds.isEmpty()) {
            throw NotFoundRowException("marketAccountId 를 찾을 수 없습니다.")
        }
        if (!hasStrategy) {
            throw NotFoundRowException("strategyId 를 찾을 수 없습니다.")
        }
        if (hasUserStrategy) {
            throw IllegalArgumentException("이미 등록한 전략입니다.")
        }

        val savedProducts = productPort.getProducts(
            marketIds = marketIds,
            productType = userStrategy.productType,
            productCodes = userStrategy.productCodes
        )
        userStrategy.productCodes.subtract(savedProducts.map { it.code }.toSet()).let {
            if (it.isNotEmpty()) {
                throw NotFoundRowException("productCode 를 찾을 수 없습니다. (${it})")
            }
        }

        val userStrategyId = userStrategyPort.setUserStrategy(
            marketServerAccountId = userStrategy.marketAccountId,
            strategyClassName = userStrategy.strategyClassName,
            productType = userStrategy.productType,
            productCategory = userStrategy.productCategory,
        )

        userStrategyProductPort.setUserStrategyProducts(
            userStrategyId = userStrategyId,
            productIds = savedProducts.map { it.id }
        )
        return@withContext true
    }
}