package com.newy.algotrade.web_flux.user_strategy.adapter.out.persistence.repository

import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.product_price.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserStrategyProductRepository : CoroutineCrudRepository<UserStrategyProductR2dbcEntity, Long> {
    @Query(
        """
        SELECT uts.id           AS user_trade_strategy_id
             , ts.class_name    AS strategy_class_name
             , m.code           AS market_code
             , uts.product_type AS product_type
             , p.code           AS product_code
             , uts.time_frame   AS time_frame
        FROM   user_trade_strategy_product utsp
        INNER JOIN user_trade_strategy uts   ON uts.id = utsp.user_trade_strategy_id
        INNER JOIN trade_strategy ts         ON ts.id = uts.trade_strategy_id
        INNER JOIN market_server_account msa ON msa.id = uts.market_server_account_id
        INNER JOIN market_server ms          ON ms.id = msa.market_server_id
        INNER JOIN market m                  ON m.id = ms.market_id
        INNER JOIN product p                 ON p.id = utsp.product_id
        ORDER BY uts.id
               , utsp.sort
        ;
    """
    )
    suspend fun findAllAsUserStrategyKey(): Flow<UserStrategyKey>

    @Query(
        """
        SELECT uts.id           AS user_trade_strategy_id
             , ts.class_name    AS strategy_class_name
             , m.code           AS market_code
             , uts.product_type AS product_type
             , p.code           AS product_code
             , uts.time_frame   AS time_frame
        FROM   user_trade_strategy_product utsp
        INNER JOIN user_trade_strategy uts   ON uts.id = utsp.user_trade_strategy_id
        INNER JOIN trade_strategy ts         ON ts.id = uts.trade_strategy_id
        INNER JOIN market_server_account msa ON msa.id = uts.market_server_account_id
        INNER JOIN market_server ms          ON ms.id = msa.market_server_id
        INNER JOIN market m                  ON m.id = ms.market_id
        INNER JOIN product p                 ON p.id = utsp.product_id
        WHERE  uts.id = :userStrategyId
        ORDER BY uts.id
               , utsp.sort
        ;
    """
    )
    suspend fun findByUserStrategyIdAsUserStrategyKey(userStrategyId: Long): Flow<UserStrategyKey>
}

@Table("user_trade_strategy_product")
data class UserStrategyProductR2dbcEntity(
    @Id val id: Long = 0,
    @Column("user_trade_strategy_id") val userStrategyId: Long,
    val productId: Long,
    val sort: Int,
)

@ReadingConverter
class UserStrategyKeyReadingConverter : Converter<Row, UserStrategyKey> {
    override fun convert(source: Row): UserStrategyKey {
        return UserStrategyKey(
            userStrategyId = source.get("user_trade_strategy_id") as Long,
            strategyClassName = source.get("strategy_class_name") as String,
            productPriceKey = ProductPriceKey(
                market = Market.valueOf(source.get("market_code") as String),
                productType = ProductType.valueOf(source.get("product_type") as String),
                productCode = source.get("product_code") as String,
                interval = Candle.TimeFrame.valueOf(source.get("time_frame") as String).timePeriod,
            )
        )
    }
}