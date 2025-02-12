package com.newy.algotrade.integration.market_account.adapter.out.external_system

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.market_account.adapter.out.external_system.ValidMarketAccountAdapter
import helpers.BaseDisabledTest
import helpers.TestEnv
import helpers.spring.BaseDataR2dbcTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByBitSuccessValidMarketAccountAdapterTest(
    @Autowired private val adapter: ValidMarketAccountAdapter,
) : BaseDisabledTest, BaseDataR2dbcTest() {
    @DisabledIf("hasNotByBitApiInfo")
    @Test
    fun `유효한 appKey appSecret 을 사용하면 인증에 성공한다`() = runBlocking {
        val isValidated = adapter.validMarketAccount(
            marketCode = MarketCode.BY_BIT,
            privateApiInfo = PrivateApiInfo(
                appKey = TestEnv.ByBit.apiKey,
                appSecret = TestEnv.ByBit.apiSecret,
            )
        )

        assertTrue(isValidated)
    }

    @DisabledIf("hasNotByBitApiInfo")
    @Test
    fun `유효하지 않은 appKey appSecret 을 사용하면 인증에 실패한다`() = runBlocking {
        val isValidated = adapter.validMarketAccount(
            marketCode = MarketCode.BY_BIT,
            privateApiInfo = PrivateApiInfo(
                appKey = TestEnv.ByBit.apiKey + "A",
                appSecret = TestEnv.ByBit.apiSecret + "A",
            )
        )

        assertFalse(isValidated)
    }
}

class LsSecValidMarketAccountAdapterTest(
    @Autowired private val adapter: ValidMarketAccountAdapter,
) : BaseDisabledTest, BaseDataR2dbcTest() {
    @DisabledIf("hasNotByBitApiInfo")
    @Test
    fun `유효한 appKey appSecret 을 사용하면 인증에 성공한다`() = runBlocking {
        val isValidated = adapter.validMarketAccount(
            marketCode = MarketCode.LS_SEC,
            privateApiInfo = PrivateApiInfo(
                appKey = TestEnv.LsSec.apiKey,
                appSecret = TestEnv.LsSec.apiSecret,
            )
        )

        assertTrue(isValidated)
    }

    @DisabledIf("hasNotByBitApiInfo")
    @Test
    fun `유효하지 않은 appKey appSecret 을 사용하면 인증에 실패한다`() = runBlocking {
        val isValidated = adapter.validMarketAccount(
            marketCode = MarketCode.LS_SEC,
            privateApiInfo = PrivateApiInfo(
                appKey = TestEnv.LsSec.apiKey + "A",
                appSecret = TestEnv.LsSec.apiSecret + "A",
            )
        )

        assertFalse(isValidated)
    }
}