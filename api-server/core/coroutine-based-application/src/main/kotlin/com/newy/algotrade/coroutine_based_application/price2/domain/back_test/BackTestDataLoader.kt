package com.newy.algotrade.coroutine_based_application.price2.domain.back_test

import com.newy.algotrade.coroutine_based_application.common.coroutine.Polling
import com.newy.algotrade.coroutine_based_application.common.coroutine.PollingCallback
import com.newy.algotrade.coroutine_based_application.price2.port.out.GetProductPricePort
import com.newy.algotrade.coroutine_based_application.price2.port.out.model.GetProductPriceParam
import com.newy.algotrade.domain.chart.Candle
import com.newy.algotrade.domain.chart.DEFAULT_CANDLE_SIZE
import com.newy.algotrade.domain.common.consts.Market
import com.newy.algotrade.domain.common.extension.ProductPrice
import com.newy.algotrade.domain.price.domain.model.ProductPriceKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import kotlin.coroutines.CoroutineContext

class BackTestDataLoader(
    private val loader: GetProductPricePort,
    private val startDateTime: OffsetDateTime,
    private val endDateTime: OffsetDateTime,
    private val coroutineContext: CoroutineContext,
    override var callback: PollingCallback<ProductPriceKey, List<ProductPrice>>? = null,
) : GetProductPricePort, Polling<ProductPriceKey, List<ProductPrice>> {
    private val cache = FileCache()
    private val finish = Channel<Unit>()

    override suspend fun getProductPrices(param: GetProductPriceParam): List<ProductPrice> {
        val list = loadProductPrices(
            Key(
                param.productPriceKey,
                startDateTime,
                endDateTime
            )
        )

        return list.slice(0 until param.limit).also {
            CoroutineScope(coroutineContext).launch {
                list.subList(param.limit, list.size).forEach {
                    callback?.invoke(param.productPriceKey to listOf(it))
                }
                finish.send(Unit)
            }
        }
    }

    suspend fun awaitFinish() = finish.receive()

    override fun cancel() {
        coroutineContext.cancelChildren()
    }

    suspend fun loadProductPrices(key: Key): List<Candle> {
        val hitting = cache.load(key)
        if (hitting.isNotEmpty()) {
            return hitting
        }

        return fetchData(key).also {
            cache.insert(key, it)
        }
    }

    private suspend fun fetchData(key: Key): List<Candle> {
        key.run {
            val results = mutableSetOf<Candle>().also {
                it.addAll(
                    loader.getProductPrices(
                        GetProductPriceParam(
                            productPriceKey,
                            startDateTime,
                            if (productPriceKey.market == Market.BY_BIT) DEFAULT_CANDLE_SIZE else 399
                        )
                    )
                )
            }
            val firstBeginTime = results.first().time.begin

            var end = endDateTime
            while (startDateTime.isBefore(end)) {
                val list = loader.getProductPrices(
                    GetProductPriceParam(
                        productPriceKey,
                        end,
                        if (productPriceKey.market == Market.BY_BIT) 1000 else 500
                    )
                )
                end = list.first().time.begin

                println("current end: $end")
                println("startDateTime.isBefore(end): ${startDateTime.isBefore(end)}")
                results.addAll(list)
            }

            return results
                .sortedBy { it.time.begin }
                .filter { it.time.begin >= firstBeginTime }
                .toList()
        }
    }

    override suspend fun start() {}
    override fun unSubscribe(key: ProductPriceKey) {}
    override suspend fun subscribe(key: ProductPriceKey) {}

    data class Key(
        val productPriceKey: ProductPriceKey,
        val startDateTime: OffsetDateTime,
        val endDateTime: OffsetDateTime
    )
}