package com.newy.algotrade.unit.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product.adapter.out.volatile_storage.InMemoryCandleStoreAdapter
import com.newy.algotrade.coroutine_based_application.product.port.`in`.CandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.CandlePort
import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.volatile_storage.InMemoryStrategyStoreAdapter
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunnableStrategyCommandService
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.product.ProductPriceKey
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import helpers.productPrice
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

@DisplayName("1개 RunnableStrategy 등록/삭제 테스트")
class RunnableStrategyTest : BaseRunnableStrategyTest() {
    @Test
    fun `RunnableStrategy 등록하기`() = runTest {
        service.setRunnableStrategy(userStrategyKey)

        assertEquals(1, candlePort.getCandles(productPriceKey).size)
    }

    @Test
    fun `RunnableStrategy 삭제하기`() = runTest {
        service.setRunnableStrategy(userStrategyKey)
        service.removeRunnableStrategy(userStrategyKey)

        assertEquals(
            0,
            candlePort.getCandles(productPriceKey).size,
            "더이상 candle 을 사용하는 strategy 가 없어서, candle 이 삭제된다."
        )
    }
}

@DisplayName("여러 개 RunnableStrategy 등록/삭제 테스트")
class ManyRunnableStrategyTest : BaseRunnableStrategyTest() {
    @BeforeEach
    fun initTestData(): Unit = runBlocking {
        service.setRunnableStrategy(userStrategyKey)
    }

    @Test
    fun `같은 productPriceKey 를 사용하는 RunnableStrategy 를 등록하고, 삭제하기`() = runTest {
        val sameProductPriceKey = productPriceKey
        service.setRunnableStrategy(
            userStrategyKey.copy(
                userStrategyId = "test2",
                productPriceKey = sameProductPriceKey
            )
        )

        service.removeRunnableStrategy(userStrategyKey)

        assertEquals(
            1,
            candlePort.getCandles(productPriceKey).size,
            "userStrategyKey2 가 같은 candle(BTCUSDT) 를 사용하기 때문에 삭제되지 않는다."
        )
    }

    @Test
    fun `다른 productPriceKey 를 사용하는  RunnableStrategy 를 등록하고, 삭제하기`() = runTest {
        val differentProductPriceKey = productPriceKey.copy(productCode = "ETHUSDT")
        service.setRunnableStrategy(
            userStrategyKey.copy(
                userStrategyId = "test2",
                productPriceKey = differentProductPriceKey
            )
        )

        service.removeRunnableStrategy(userStrategyKey)

        assertEquals(
            0,
            candlePort.getCandles(productPriceKey).size,
            "userStrategyKey2 가 다른 candle(ETHUSDT) 를 사용하기 때문에 삭제되지 않는다."
        )
    }
}

open class BaseRunnableStrategyTest {
    protected val productPriceKey = productPriceKey("BTCUSDT")
    protected val userStrategyKey = UserStrategyKey(
        userStrategyId = "test1",
        strategyClassName = "BuyTripleRSIStrategy",
        productPriceKey = productPriceKey,
    )
    protected lateinit var candlePort: CandlePort
    protected lateinit var service: RunnableStrategyUseCase

    @BeforeEach
    fun setUp() {
        candlePort = InMemoryCandleStoreAdapter()
        service = RunnableStrategyCommandService(
            candlesUseCase = DefaultCandlesUseCase(candlePort),
            strategyPort = InMemoryStrategyStoreAdapter()
        )
    }
}

private class DefaultCandlesUseCase(
    private val candlePort: CandlePort
) : CandlesUseCase {
    override suspend fun setCandles(productPriceKey: ProductPriceKey): Candles =
        candlePort.setCandles(
            key = productPriceKey,
            listOf(productPrice(1000, Duration.ofMinutes(1)))
        )

    override fun removeCandles(productPriceKey: ProductPriceKey) {
        candlePort.removeCandles(productPriceKey)
    }

    override fun addCandles(productPriceKey: ProductPriceKey, candleList: List<ProductPrice>): Candles {
        TODO("Not yet implemented")
    }
}