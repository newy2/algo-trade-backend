package com.newy.algotrade.coroutine_based_application.price2.port.`in`

import com.newy.algotrade.coroutine_based_application.price2.port.out.*
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.Candles
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import java.time.OffsetDateTime

class RegisterCandleUseCase(
    private val productPricePort: GetProductPricePort,
    private val pollingProductPricePort: SubscribePollingProductPricePort,
    private val candlePort: CandlePort,
    private val initDataSize: Int = DEFAULT_CANDLE_SIZE
) {
    suspend fun register(productPriceKey: ProductPriceKey) {
        fetchInitCandles(productPriceKey)
        pollingCandles(productPriceKey)
    }

    private suspend fun fetchInitCandles(productPriceKey: ProductPriceKey) {
        if (candlePort.getCandles(productPriceKey).size != 0) {
            return
        }

        productPricePort.getProductPrices(
            GetProductPriceParam(
                productPriceKey,
                OffsetDateTime.now(),
                initDataSize,
            )
        ).let {
            candlePort.setCandles(productPriceKey, it)
        }
    }

    private suspend fun pollingCandles(productPriceKey: ProductPriceKey) {
        pollingProductPricePort.subscribe(productPriceKey)
    }
}

class UnRegisterCandleUseCase(
    private val userStrategyPort: HasUserStrategyPort,
    private val candlePort: DeleteCandlePort,
    private val pollingProductPricePort: UnSubscribePollingProductPricePort,
) {
    fun unRegister(productPriceKey: ProductPriceKey) {
        if (userStrategyPort.hasProductPriceKey(productPriceKey)) {
            return
        }

        candlePort.deleteCandles(productPriceKey)
        pollingProductPricePort.unSubscribe(productPriceKey)
    }
}

interface AddCandleUseCase {
    fun addCandle(productPriceKey: ProductPriceKey, candleList: List<Candle>): Candles
}