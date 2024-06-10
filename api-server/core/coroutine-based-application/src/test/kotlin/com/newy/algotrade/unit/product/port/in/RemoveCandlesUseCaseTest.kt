package com.newy.algotrade.unit.product.port.`in`

import com.newy.algotrade.coroutine_based_application.product.port.`in`.RemoveCandlesUseCase
import com.newy.algotrade.coroutine_based_application.product.port.out.HasStrategyPort
import com.newy.algotrade.coroutine_based_application.product.port.out.RemoveCandlePort
import com.newy.algotrade.coroutine_based_application.product.port.out.UnSubscribePollingProductPricePort
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import helpers.productPriceKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

class RemoveCandlesUseCaseTest : HasStrategyPort, RemoveCandlePort, UnSubscribePollingProductPricePort {
    private lateinit var service: RemoveCandlesUseCase
    private var deleteCandleCount = 0
    private var unSubscribeCount = 0

    override fun removeCandles(key: ProductPriceKey) {
        deleteCandleCount++
    }

    override fun unSubscribe(key: ProductPriceKey) {
        unSubscribeCount++
    }

    override fun hasProductPriceKey(key: ProductPriceKey): Boolean {
        val storedList = listOf(
            productPriceKey("BTCUSDT", Duration.ofMinutes(1))
        )

        return storedList.contains(key)
    }

    @BeforeEach
    fun setUp() {
        service = RemoveCandlesUseCase(
            strategyPort = this,
            candlePort = this,
            pollingProductPricePort = this
        )
        deleteCandleCount = 0
        unSubscribeCount = 0
    }

    @Test
    fun `다른 사용자가 사용 중인 ProductPriceKey 로 unRegister 하는 경우`() = runBlocking {
        val storedProductPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))

        service.removeCandles(storedProductPriceKey)

        Assertions.assertEquals(0, deleteCandleCount)
        Assertions.assertEquals(0, unSubscribeCount)
    }

    @Test
    fun `사용하는 사용자가 없는 ProductPriceKey 로 unRegister 하는 경우`() = runBlocking {
        val unStoredProductPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(5))

        service.removeCandles(unStoredProductPriceKey)

        Assertions.assertEquals(1, deleteCandleCount)
        Assertions.assertEquals(1, unSubscribeCount)
    }
}