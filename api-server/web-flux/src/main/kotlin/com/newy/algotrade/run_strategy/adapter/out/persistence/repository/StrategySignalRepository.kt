package com.newy.algotrade.run_strategy.adapter.out.persistence.repository

import com.newy.algotrade.chart.domain.Candle
import com.newy.algotrade.chart.domain.order.OrderType
import com.newy.algotrade.chart.domain.strategy.StrategySignal
import kotlinx.coroutines.flow.Flow
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Limit
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

interface StrategySignalRepository : CoroutineCrudRepository<StrategySignalR2dbcEntity, Long> {
    suspend fun findAllByUserStrategyIdAndProductIdOrderByIdDesc(
        userStrategyId: Long,
        productId: Long,
        limit: Limit
    ): Flow<StrategySignalR2dbcEntity>
}

@Table("user_trade_strategy_signal")
data class StrategySignalR2dbcEntity(
    @Id val id: Long = 0,
    @Column("user_trade_strategy_id") val userStrategyId: Long,
    val productId: Long,
    val orderType: OrderType,
    val price: BigDecimal,
    val candleBeginTime: LocalDateTime,
    val candleInterval: Candle.TimeFrame
) {
    fun toDomainEntity() =
        StrategySignal(
            orderType = orderType,
            timeFrame = Candle.TimeRange(
                period = candleInterval.timePeriod,
                begin = candleBeginTime.atOffset(ZoneOffset.UTC),
            ),
            price = price
        )
}