package com.newy.algotrade.unit.run_strategy.service

import com.newy.algotrade.coroutine_based_application.product_price.adapter.out.volatile_storage.InMemoryCandlesStoreAdapter
import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.RemoveCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product_price.port.`in`.SetCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product_price.port.out.CandlesPort
import com.newy.algotrade.coroutine_based_application.run_strategy.adapter.out.volatile_storage.InMemoryStrategyStoreAdapter
import com.newy.algotrade.coroutine_based_application.run_strategy.port.`in`.RunnableStrategyUseCase
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.DeleteStrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.IsStrategyUsingProductPriceKeyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.SaveStrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.port.out.StrategyPort
import com.newy.algotrade.coroutine_based_application.run_strategy.service.RunnableStrategyCommandService
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.product_price.ProductPriceKey
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

        assertEquals(1, candlesPort.findCandles(productPriceKey).size)
    }

    @Test
    fun `RunnableStrategy 삭제하기`() = runTest {
        service.setRunnableStrategy(userStrategyKey)
        service.removeRunnableStrategy(userStrategyKey)

        assertEquals(
            0,
            candlesPort.findCandles(productPriceKey).size,
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
                userStrategyId = 2,
                productPriceKey = sameProductPriceKey
            )
        )

        service.removeRunnableStrategy(userStrategyKey)

        assertEquals(
            1,
            candlesPort.findCandles(productPriceKey).size,
            "userStrategyKey2 가 같은 candle(BTCUSDT) 를 사용하기 때문에 삭제되지 않는다."
        )
    }

    @Test
    fun `다른 productPriceKey 를 사용하는  RunnableStrategy 를 등록하고, 삭제하기`() = runTest {
        val differentProductPriceKey = productPriceKey.copy(productCode = "ETHUSDT")
        service.setRunnableStrategy(
            userStrategyKey.copy(
                userStrategyId = 2,
                productPriceKey = differentProductPriceKey
            )
        )

        service.removeRunnableStrategy(userStrategyKey)

        assertEquals(
            0,
            candlesPort.findCandles(productPriceKey).size,
            "userStrategyKey2 가 다른 candle(ETHUSDT) 를 사용하기 때문에 삭제되지 않는다."
        )
    }
}

open class BaseRunnableStrategyTest {
    protected val productPriceKey = productPriceKey("BTCUSDT")
    protected val userStrategyKey = UserStrategyKey(
        userStrategyId = 1,
        strategyClassName = "BuyTripleRSIStrategy",
        productPriceKey = productPriceKey,
    )
    protected lateinit var candlesPort: CandlesPort
    protected lateinit var service: RunnableStrategyUseCase

    @BeforeEach
    fun setUp() {
        candlesPort = InMemoryCandlesStoreAdapter()
        service = newRunnableStrategyCommandService(
            candlesPort = candlesPort
        )
    }
}

private fun newRunnableStrategyCommandService(
    candlesPort: CandlesPort = InMemoryCandlesStoreAdapter(),
    strategyPort: StrategyPort = InMemoryStrategyStoreAdapter(),

    setCandlesUseCase: SetCandlesUseCase = DefaultCandlesUseCase(candlesPort),
    removeCandlesUseCase: RemoveCandlesUseCase = DefaultCandlesUseCase(candlesPort),
    saveStrategyPort: SaveStrategyPort = strategyPort,
    deleteStrategyPort: DeleteStrategyPort = strategyPort,
    isStrategyUsingProductPriceKeyPort: IsStrategyUsingProductPriceKeyPort = strategyPort,
) = RunnableStrategyCommandService(
    setCandlesUseCase = setCandlesUseCase,
    removeCandlesUseCase = removeCandlesUseCase,
    saveStrategyPort = saveStrategyPort,
    deleteStrategyPort = deleteStrategyPort,
    isStrategyUsingProductPriceKeyPort = isStrategyUsingProductPriceKeyPort,
)

private class DefaultCandlesUseCase(
    private val candlesPort: CandlesPort
) : SetCandlesUseCase, RemoveCandlesUseCase {
    override suspend fun setCandles(productPriceKey: ProductPriceKey): Candles =
        candlesPort.saveWithReplaceCandles(
            key = productPriceKey,
            listOf(productPrice(1000, Duration.ofMinutes(1)))
        )

    override fun removeCandles(productPriceKey: ProductPriceKey) {
        candlesPort.deleteCandles(productPriceKey)
    }
}