package com.newy.algotrade.unit.product.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.product.adapter.out.persistent.InMemoryStrategyStore
import com.newy.algotrade.coroutine_based_application.product.port.`in`.strategy.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.product.port.out.StrategyPort
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue


open class BaseTest {
    protected fun createStrategy(key: UserStrategyKey): Strategy {
        return Strategy.create(key.strategyId, DEFAULT_CHART_FACTORY.candles())
    }

    protected fun createUserStrategyKey(userStrategyId: String, productPriceKey: ProductPriceKey): UserStrategyKey {
        return UserStrategyKey(
            userStrategyId,
            StrategyId.BuyTripleRSIStrategy,
            productPriceKey
        )
    }

    protected fun createProductPriceKey(productCode: String): ProductPriceKey {
        return productPriceKey(productCode, Duration.ofMinutes(1))
    }
}

@DisplayName("InMemoryStrategyStoreTest 기본 기능 테스트")
class InMemoryStrategyStoreTest : BaseTest() {
    private val productPriceKey = createProductPriceKey("BTCUSDT")
    private val userStrategyKey = createUserStrategyKey("id1", productPriceKey)
    private val strategy = createStrategy(userStrategyKey)
    private lateinit var store: StrategyPort

    @BeforeEach
    fun setUp() {
        store = InMemoryStrategyStore()
        store.addStrategy(userStrategyKey, strategy)
    }

    @Test
    fun `productPriceKey 로 등록된 strategy 가 있는지 확인하는 방법`() {
        assertTrue(store.hasProductPriceKey(productPriceKey))
    }

    @Test
    fun `strategy 삭제하기`() {
        store.removeStrategy(userStrategyKey)

        assertFalse(store.hasProductPriceKey(productPriceKey))
    }

    @Test
    fun `productPriceKey 에 매칭되는 strategy 맵 가져오기`() {
        val filteredMap = store.filterBy(productPriceKey)

        assertEquals(mapOf(userStrategyKey to strategy), filteredMap)
    }

    @Test
    fun `같은 productPriceKey 를 가진 사용자 전략을 추가한 경우`() {
        val sameProduct = createProductPriceKey("BTCUSDT")
        val userStrategyKey2 = createUserStrategyKey("id2", sameProduct)
        val strategy2 = createStrategy(userStrategyKey2)

        store.addStrategy(userStrategyKey2, strategy2)
        val filteredMap = store.filterBy(createProductPriceKey("BTCUSDT"))

        assertEquals(
            mapOf(
                userStrategyKey to strategy,
                userStrategyKey2 to strategy2
            ),
            filteredMap
        )
    }

    @Test
    fun `다른 productPriceKey 를 가진 사용자 전략을 추가한 경우`() {
        val differentProduct = createProductPriceKey("ETHUSDT")
        val userStrategyKey2 = createUserStrategyKey("id2", differentProduct)
        val strategy2 = createStrategy(userStrategyKey2)

        store.addStrategy(userStrategyKey2, strategy2)
        val filteredMap = store.filterBy(createProductPriceKey("BTCUSDT"))

        assertEquals(mapOf(userStrategyKey to strategy), filteredMap)
    }
}