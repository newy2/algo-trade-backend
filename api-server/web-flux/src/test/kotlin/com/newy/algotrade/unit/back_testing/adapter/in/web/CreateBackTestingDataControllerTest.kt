package com.newy.algotrade.unit.back_testing.adapter.`in`.web

import com.newy.algotrade.back_testing.adapter.`in`.web.CreateBackTestingDataController
import com.newy.algotrade.back_testing.domain.BackTestingDataKey
import com.newy.algotrade.back_testing.port.`in`.CreateBackTestingDataUseCase
import com.newy.algotrade.back_testing.port.`in`.SetBackTestingDataUseCase
import com.newy.algotrade.common.consts.Market
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.common.extension.ProductPrice
import com.newy.algotrade.product_price.domain.ProductPriceKey
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.OffsetDateTime

class CreateBackTestingDataControllerTest : CreateBackTestingDataUseCase, SetBackTestingDataUseCase {
    private var log: String = ""

    override suspend fun createData(key: BackTestingDataKey, seedSize: Int): List<ProductPrice> {
        log += "createData "
        return emptyList()
    }

    override suspend fun setBackTestingData(key: BackTestingDataKey, list: List<ProductPrice>): Boolean {
        log += "setBackTestingData "
        return false
    }

    @Test
    fun `UseCase 호출 순서 확인`() = runTest {
        val controller = CreateBackTestingDataController(
            this@CreateBackTestingDataControllerTest,
            this@CreateBackTestingDataControllerTest
        )

        val key = BackTestingDataKey(
            ProductPriceKey(
                Market.BY_BIT,
                ProductType.SPOT,
                "BTCUSDT",
                Duration.ofMinutes(1)
            ),
            searchBeginTime = OffsetDateTime.parse("8888-01-01T00:00+09:00"),
            searchEndTime = OffsetDateTime.parse("8888-01-01T00:01+09:00"),
        )

        controller.createBackTestingData(key)

        assertEquals("createData setBackTestingData ", log)
    }
}