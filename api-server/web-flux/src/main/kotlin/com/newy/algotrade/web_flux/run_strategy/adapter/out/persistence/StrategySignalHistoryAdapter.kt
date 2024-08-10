package com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.product.ProductPriceKey
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Limit

@PersistenceAdapter
class StrategySignalHistoryAdapter(
    private val strategyRepository: StrategyRepositoryForRunStrategy,
    private val productRepository: ProductRepositoryForRunStrategy,
    private val strategySignalRepository: StrategySignalRepository,
) : StrategySignalHistoryPort {
    override suspend fun getHistory(key: StrategySignalHistoryKey, maxSize: Int): StrategySignalHistory =
        withContext(Dispatchers.IO) {
            val productId = getProductId(key.productPriceKey)

            val (signals, strategy) = listOf(
                async {
                    strategySignalRepository.findAllByUserStrategyIdAndProductIdOrderByIdDesc(
                        userStrategyId = key.userStrategyId,
                        productId = productId,
                        limit = Limit.of(maxSize)
                    ).toList()
                },
                async {
                    strategyRepository.findByUserStrategyId(userStrategyId = key.userStrategyId)
                }
            ).awaitAll().let {
                Pair(it[0] as List<StrategySignalR2dbcEntity>, it[1] as StrategyR2dbcEntity)
            }

            return@withContext StrategySignalHistory().also {
                signals
                    .takeIf { it.isNotEmpty() }
                    ?.reversed()
                    ?.let {
                        if (it.first().orderType == strategy.entryOrderType) {
                            it
                        } else {
                            it.subList(1, it.size)
                        }
                    }
                    ?.forEach { eachSignal ->
                        it.add(eachSignal.toDomainEntity())
                    }
            }
        }

    override suspend fun addHistory(key: StrategySignalHistoryKey, signal: StrategySignal) {
        val productId = getProductId(key.productPriceKey)

        val strategySignal = StrategySignalR2dbcEntity(
            userStrategyId = key.userStrategyId,
            productId = productId,
            orderType = signal.orderType,
            candleBeginTime = signal.timeFrame.begin.toLocalDateTime(),
            candleInterval = Candle.TimeFrame.from(signal.timeFrame.period)!!,
            price = signal.price,
        )

        strategySignalRepository.save(strategySignal)
    }

    private suspend fun getProductId(productPriceKey: ProductPriceKey): Long {
        return productRepository.findProductId(
            market = productPriceKey.market,
            productType = productPriceKey.productType,
            productCode = productPriceKey.productCode,
        )
            ?: throw NotFoundRowException("product 를 찾을 수 없습니다. (market: ${productPriceKey.market}, productType: ${productPriceKey.productType}, productCode: ${productPriceKey.productCode})")
    }

    override suspend fun removeHistory(key: StrategySignalHistoryKey) {
        TODO("Not yet implemented")
    }
}