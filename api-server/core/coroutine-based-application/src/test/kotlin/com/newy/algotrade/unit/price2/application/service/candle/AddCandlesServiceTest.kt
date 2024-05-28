package com.newy.algotrade.unit.price2.application.service.candle

import com.newy.algotrade.coroutine_based_application.price2.adapter.out.persistent.InMemoryCandleStore
import com.newy.algotrade.coroutine_based_application.price2.application.service.candle.AddCandlesService
import com.newy.algotrade.coroutine_based_application.price2.port.`in`.candle.AddCandlesUseCase
import com.newy.algotrade.coroutine_based_application.price2.port.out.CandlePort
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

private val now = OffsetDateTime.parse("2024-05-01T00:00Z")

// TODO 테스트 헬퍼 메소드로 옮기자
private fun productPrice(amount: Int, interval: Duration, beginTime: OffsetDateTime = now) =
    Candle.TimeFrame.from(interval)!!(
        beginTime,
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        amount.toBigDecimal(),
        0.toBigDecimal(),
    )

private fun productPriceKey(productCode: String, interval: Duration) =
    if (productCode == "BTCUSDT")
        ProductPriceKey(Market.BY_BIT, ProductType.SPOT, productCode, interval)
    else
        ProductPriceKey(Market.E_BEST, ProductType.SPOT, productCode, interval)


@DisplayName("캔들 저장 테스트")
class AddCandlesServiceTest {
    private val productPriceKey = productPriceKey("BTCUSDT", Duration.ofMinutes(1))
    private val productPriceList = listOf(
        productPrice(1000, Duration.ofMinutes(1), now.plusMinutes(0)),
        productPrice(2000, Duration.ofMinutes(1), now.plusMinutes(1)),
    )

    private lateinit var nextPrice: ListIterator<ProductPrice>
    private lateinit var candleStore: CandlePort
    private lateinit var service: AddCandlesUseCase

    @BeforeEach
    fun setUp() {
        nextPrice = productPriceList.listIterator()
        candleStore = InMemoryCandleStore().also {
            it.setCandles(productPriceKey, listOf(nextPrice.next()))
        }
        service = AddCandlesService(candleStore)
    }

    @Test
    fun `candles 업데이트 하는 경우`() {
        val candles = service.addCandles(productPriceKey, listOf(nextPrice.next()))

        candles.let {
            Assertions.assertEquals(2, it.size)
            Assertions.assertEquals(productPriceList[0], it[0])
            Assertions.assertEquals(productPriceList[1], it[1])
        }
    }

    @Test
    fun `제거된 candles 업데이트 하는 경우 - 업데이트가 무시된다`() {
        candleStore.deleteCandles(productPriceKey)

        val candles = service.addCandles(productPriceKey, listOf(nextPrice.next()))

        Assertions.assertEquals(0, candles.size)
    }
}