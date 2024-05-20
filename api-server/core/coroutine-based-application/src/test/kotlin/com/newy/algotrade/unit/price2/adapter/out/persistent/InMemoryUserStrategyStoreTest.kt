package com.newy.algotrade.unit.price2.adapter.out.persistent

import com.newy.algotrade.coroutine_based_application.price2.adpter.out.persistent.InMemoryUserStrategyStore
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.model.UserStrategyKey
import com.newy.algotrade.coroutine_based_application.price2.port.out.UserStrategyPort
import com.newy.algotrade.domain.chart.DEFAULT_CHART_FACTORY
import com.newy.algotrade.domain.chart.strategy.Strategy
import com.newy.algotrade.domain.chart.strategy.StrategyId
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import com.newy.algotrade.unit.price2.port.`in`.productPriceKey
import org.junit.jupiter.api.Assertions
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

    protected fun createUserStrategyKey(userId: String, productPriceKey: ProductPriceKey): UserStrategyKey {
        return UserStrategyKey(
            userId,
            StrategyId.BuyTripleRSIStrategy,
            productPriceKey
        )
    }

    protected fun createProductPriceKey(productCode: String): ProductPriceKey {
        return productPriceKey(productCode, Duration.ofMinutes(1))
    }
}

@DisplayName("InMemoryUserStrategyStore 기본 기능 테스트")
class SingleElementInMemoryUserStrategyStoreTest : BaseTest() {
    private val productPriceKey = createProductPriceKey("BTCUSDT")
    private val key = createUserStrategyKey("user1", productPriceKey)
    private val strategy = createStrategy(key)
    private lateinit var store: UserStrategyPort

    @BeforeEach
    fun setUp() {
        store = InMemoryUserStrategyStore()
        store.add(key, strategy)
    }

    @Test
    fun `productPriceKey 로 등록된 strategy 가 있는지 확인하는 방법`() {
        assertTrue(store.hasProductPriceKey(productPriceKey))
    }

    @Test
    fun `strategy 삭제하기`() {
        store.remove(key)

        Assertions.assertFalse(store.hasProductPriceKey(productPriceKey))
    }

    @Test
    fun `productPriceKey 에 매칭되는 strategy 리스트 가져오기`() {
        val list = store.getStrategyList(productPriceKey)

        assertEquals(1, list.size)
        assertEquals(listOf(strategy), list)
    }
}

@DisplayName("element 가 여러 개인 경우 InMemoryUserStrategyStore 테스트")
class MultipleElementsInMemoryUserStrategyStoreTest : BaseTest() {
    private val key1 = createUserStrategyKey("user1", createProductPriceKey("BTCUSDT"))
    private val strategy1 = createStrategy(key1)
    private lateinit var store: UserStrategyPort

    @BeforeEach
    fun setUp() {
        store = InMemoryUserStrategyStore()
    }

    @Test
    fun `같은 상품을 등록한 경우`() {
        val key2 = createUserStrategyKey("user2", createProductPriceKey("BTCUSDT"))
        val strategy2 = createStrategy(key2)
        store.add(key1, strategy1)
        store.add(key2, strategy2)

        val list = store.getStrategyList(createProductPriceKey("BTCUSDT"))

        assertEquals(listOf(strategy1, strategy2), list)
    }

    @Test
    fun `다른 상품을 등록한 경우`() {
        val key2 = createUserStrategyKey("user2", createProductPriceKey("ETHUSDT"))
        val strategy2 = createStrategy(key2)
        store.add(key1, strategy1)
        store.add(key2, strategy2)

        val list = store.getStrategyList(createProductPriceKey("BTCUSDT"))

        assertEquals(listOf(strategy1), list)
    }
}

@DisplayName("빈 InMemoryUserStrategyStore 테스트")
class EmptyElementInMemoryUserStrategyStoreTest : BaseTest() {
    private val noAdded = createProductPriceKey("BTCUSDT")
    private lateinit var store: UserStrategyPort

    @BeforeEach
    fun setUp() {
        store = InMemoryUserStrategyStore()
    }

    @Test
    fun `hasProductPriceKey`() {
        Assertions.assertFalse(store.hasProductPriceKey(noAdded))
    }

    @Test
    fun `getStrategyList`() {
        assertEquals(emptyList(), store.getStrategyList(noAdded))
    }
}