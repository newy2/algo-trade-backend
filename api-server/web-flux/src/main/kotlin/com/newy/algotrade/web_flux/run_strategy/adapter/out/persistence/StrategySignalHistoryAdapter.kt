package com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence

import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategySignalHistoryPort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.strategy.StrategySignal
import com.newy.algotrade.domain.chart.strategy.StrategySignalHistory
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.run_strategy.StrategySignalHistoryKey
import com.newy.algotrade.web_flux.common.annotation.PersistenceAdapter
import com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence.repository.ProductRepositoryForRunStrategy
import com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence.repository.StrategyRepositoryForRunStrategy
import com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence.repository.StrategySignalR2dbcEntity
import com.newy.algotrade.web_flux.run_strategy.adapter.out.persistence.repository.StrategySignalRepository
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Limit

@PersistenceAdapter
class StrategySignalHistoryAdapter(
    private val strategyRepository: StrategyRepositoryForRunStrategy,
    private val productRepository: ProductRepositoryForRunStrategy,
    private val strategySignalRepository: StrategySignalRepository,
) : StrategySignalHistoryPort {
    override suspend fun findHistory(key: StrategySignalHistoryKey, maxSize: Int): StrategySignalHistory {
        val productId = getProductId(key.productPriceKey)
        val signals = strategySignalRepository.findAllByUserStrategyIdAndProductIdOrderByIdDesc(
            userStrategyId = key.userStrategyId,
            productId = productId,
            limit = Limit.of(maxSize)
        ).toList()
        val strategy = strategyRepository.findByUserStrategyId(userStrategyId = key.userStrategyId)

        return StrategySignalHistory().also { history ->
            signals
                .takeIf { it.isNotEmpty() }
                ?.reversed()
                ?.let {
                    if (it.first().orderType == strategy?.entryOrderType) {
                        it
                    } else {
                        it.subList(1, it.size)
                    }
                }
                ?.forEach { eachSignal ->
                    history.add(eachSignal.toDomainEntity())
                }
        }
    }

    override suspend fun saveHistory(key: StrategySignalHistoryKey, signal: StrategySignal) {
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

    override suspend fun deleteHistory(key: StrategySignalHistoryKey) {
        TODO("Not yet implemented")
    }
}