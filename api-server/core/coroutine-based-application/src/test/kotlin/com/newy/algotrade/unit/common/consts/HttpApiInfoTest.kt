package com.newy.algotrade.unit.common.consts

import com.newy.algotrade.coroutine_based_application.common.consts.ByBitHttpApiInfo
import com.newy.algotrade.coroutine_based_application.common.consts.EBestHttpApiInfo
import com.newy.algotrade.domain.common.consts.EBestTrCode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HttpApiInfoTest {
    @Test
    fun `loadProductPrice`() {
        ByBitHttpApiInfo.loadProductPrice().let { (path, rateLimit) ->
            assertEquals("/v5/market/kline", path)
            assertEquals(500, rateLimit.delayMillis)
        }
        EBestHttpApiInfo.loadProductPrice(true).let { (path, rateLimit, trCode) ->
            assertEquals("/stock/chart", path)
            assertEquals(1500, rateLimit.delayMillis)
            assertEquals(EBestTrCode.GET_PRODUCT_PRICE_BY_DAY.code, trCode)
        }
        EBestHttpApiInfo.loadProductPrice(false).let { (path, rateLimit, trCode) ->
            assertEquals("/stock/chart", path)
            assertEquals(1500, rateLimit.delayMillis)
            assertEquals(EBestTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code, trCode)
        }
    }
}