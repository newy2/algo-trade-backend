package com.newy.algotrade.unit.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.price2.port.`in`.UnRegisterCandleUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.DeleteCandlePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.HasUserStrategyPort
import com.newy.algotrade.coroutine_based_application.price2.port.out.UnSubscribePollingProductPricePort
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration

private fun productPriceKey(productCode: String, interval: Duration) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, interval)


class UnRegisterCandleUseCaseTest : HasUserStrategyPort, DeleteCandlePort, UnSubscribePollingProductPricePort {
    private lateinit var service: UnRegisterCandleUseCase
    private var deleteCandleCount = 0
    private var unSubscribeCount = 0

    override fun deleteCandles(key: ProductPriceKey) {
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
        service = UnRegisterCandleUseCase(
            userStrategyPort = this,
            candlePort = this,
            pollingProductPricePort = this
        )
        deleteCandleCount = 0
        unSubscribeCount = 0
    }

    @Test
    fun `다른 사용자가 사용 중인 ProductPriceKey 로 unRegister 하는 경우`() = runBlocking {
        val storedProductPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))

        service.unRegister(storedProductPriceKey)

        assertEquals(0, deleteCandleCount)
        assertEquals(0, unSubscribeCount)
    }

    @Test
    fun `사용하는 사용자가 없는 ProductPriceKey 로 unRegister 하는 경우`() = runBlocking {
        val unStoredProductPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(5))

        service.unRegister(unStoredProductPriceKey)

        assertEquals(1, deleteCandleCount)
        assertEquals(1, unSubscribeCount)
    }
}