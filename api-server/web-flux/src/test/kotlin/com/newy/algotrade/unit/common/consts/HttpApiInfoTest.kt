package com.newy.algotrade.unit.common.consts

import com.newy.algotrade.common.consts.ByBitHttpApiInfo
import com.newy.algotrade.common.consts.LsSecHttpApiInfo
import com.newy.algotrade.common.domain.consts.LsSecTrCode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HttpApiInfoTest {
    @Test
    fun `loadProductPrice`() {
        ByBitHttpApiInfo.loadProductPrice().let { (path, rateLimit) ->
            assertEquals("/v5/market/kline", path)
            assertEquals(500, rateLimit.delayMillis)
        }
        LsSecHttpApiInfo.loadProductPrice(true).let { (path, rateLimit, trCode) ->
            assertEquals("/stock/chart", path)
            assertEquals(1500, rateLimit.delayMillis)
            assertEquals(LsSecTrCode.GET_PRODUCT_PRICE_BY_DAY.code, trCode)
        }
        LsSecHttpApiInfo.loadProductPrice(false).let { (path, rateLimit, trCode) ->
            assertEquals("/stock/chart", path)
            assertEquals(1500, rateLimit.delayMillis)
            assertEquals(LsSecTrCode.GET_PRODUCT_PRICE_BY_MINUTE.code, trCode)
        }
    }
}