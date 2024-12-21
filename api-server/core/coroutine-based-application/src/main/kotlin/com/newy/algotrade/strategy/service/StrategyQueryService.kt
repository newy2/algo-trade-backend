package com.newy.algotrade.strategy.service

import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.common.exception.InitializedError
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import com.newy.algotrade.strategy.port.`in`.StrategyQuery
import com.newy.algotrade.strategy.port.out.ExistsStrategyPort
import com.newy.algotrade.strategy.port.out.FindAllStrategiesPort
import com.newy.algotrade.strategy.port.out.FindStrategyPort
import com.newy.algotrade.strategy.port.out.StrategyPort

open class StrategyQueryService(
    private val findAllStrategiesPort: FindAllStrategiesPort,
    private val findStrategyPort: FindStrategyPort,
    private val existsStrategyPort: ExistsStrategyPort
) : StrategyQuery {
    constructor(strategyPort: StrategyPort) : this(
        findAllStrategiesPort = strategyPort,
        findStrategyPort = strategyPort,
        existsStrategyPort = strategyPort,
    )

    override suspend fun checkRegisteredStrategyClasses() {
        findAllStrategiesPort.findAllStrategies().forEach { eachSpec ->
            try {
                val eachObject = Strategy.fromClassName(eachSpec.className, DEFAULT_CHART_FACTORY.candles())
                if (eachObject.entryType != eachSpec.entryType) {
                    throw InitializedError("entryType 이 다릅니다. (expected: ${eachSpec.entryType}, actual: ${eachObject.entryType})")
                }
            } catch (e: ClassNotFoundException) {
                throw InitializedError("Strategy 클래스를 찾을 수 없습니다. (className: ${eachSpec.className})")
            }
        }
    }

    override suspend fun getStrategyId(className: String): Long =
        findStrategyPort.findStrategy(className)?.id
            ?: throw NotFoundRowException("strategy 를 찾을 수 없습니다. (className: $className)")

    override suspend fun hasStrategy(className: String): Boolean =
        existsStrategyPort.existsStrategy(className)
}