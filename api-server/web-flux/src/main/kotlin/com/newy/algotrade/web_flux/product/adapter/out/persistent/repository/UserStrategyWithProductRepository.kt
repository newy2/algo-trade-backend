package com.newy.algotrade.web_flux.product.adapter.out.persistent.repository

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


interface UserStrategyWithProductRepository : CoroutineCrudRepository<UserStrategyWithProduct, Long> {
    @Query(
        """
        SELECT uts.id           AS id
             , ts.class_name    AS strategy_class_name
             , m.code           AS market_code
             , uts.product_type AS product_type
             , p.code           AS product_code
             , uts.time_frame   AS time_frame
        FROM   user_trade_strategy uts
        INNER JOIN trade_strategy ts ON ts.id = uts.trade_strategy_id
        INNER JOIN market_server_account msa ON msa.id = uts.market_server_account_id
        INNER JOIN market_server ms ON ms.id = msa.market_server_id
        INNER JOIN market m ON m.id = ms.market_id
        INNER JOIN user_trade_strategy_product utsp ON uts.id = utsp.user_trade_strategy_id
        INNER JOIN product p ON p.id = utsp.product_id
        ORDER BY utsp.sort;
    """
    )
    suspend fun findAllWithProducts(): Flow<UserStrategyWithProduct>

    @Query(
        """
        SELECT uts.id           AS id
             , ts.class_name    AS strategy_class_name
             , m.code           AS market_code
             , uts.product_type AS product_type
             , p.code           AS product_code
             , uts.time_frame   AS time_frame
        FROM   user_trade_strategy uts
        INNER JOIN trade_strategy ts ON ts.id = uts.trade_strategy_id
        INNER JOIN market_server_account msa ON msa.id = uts.market_server_account_id
        INNER JOIN market_server ms ON ms.id = msa.market_server_id
        INNER JOIN market m ON m.id = ms.market_id
        INNER JOIN user_trade_strategy_product utsp ON uts.id = utsp.user_trade_strategy_id
        INNER JOIN product p ON p.id = utsp.product_id
        WHERE  uts.id = :userStrategyId
        ORDER BY utsp.sort;
    """
    )
    suspend fun findWithProducts(userStrategyId: Long): Flow<UserStrategyWithProduct>
}

data class UserStrategyWithProduct(
    val id: Long,
    val strategyClassName: String,
    val marketCode: String,
    val productType: String,
    val productCode: String,
    val timeFrame: String,
)

@ReadingConverter
class UserStrategyWithProductReadConverter : Converter<Row, UserStrategyWithProduct> {
    override fun convert(source: Row): UserStrategyWithProduct =
        UserStrategyWithProduct(
            id = source.get("id") as Long,
            strategyClassName = source.get("strategy_class_name") as String,
            marketCode = source.get("market_code") as String,
            productType = source.get("product_type") as String,
            productCode = source.get("product_code") as String,
            timeFrame = source.get("time_frame") as String,
        )
}