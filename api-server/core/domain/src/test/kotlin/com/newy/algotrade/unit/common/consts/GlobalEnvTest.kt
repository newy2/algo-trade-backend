package com.newy.algotrade.unit.common.consts

import com.newy.algotrade.domain.common.consts.GlobalEnv
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class GlobalEnvTest {
    @Test
    fun `initialize 메소드를 호출하지 않은 경우`() {
        assertThrows<NoSuchElementException> { GlobalEnv.BY_BIT_WEB_URL }
        assertThrows<NoSuchElementException> { GlobalEnv.BY_BIT_WEB_SOCKET_URL }

        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_WEB_URL }
        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_WEB_SOCKET_URL }
        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_API_KEY }
        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_API_SECRET }
    }

    @Test
    fun `initialize 메소드를 호출한 경우`() {
        GlobalEnv.initialize(
            byBitWebUrl = "",
            byBitWebSocketUrl = "",
            lsSecWebUrl = "",
            lsSecWebSocketUrl = "",
            lsSecApiKey = "",
            lsSecApiSecret = "",
        )
        assertDoesNotThrow { GlobalEnv.BY_BIT_WEB_URL }
        assertDoesNotThrow { GlobalEnv.BY_BIT_WEB_SOCKET_URL }

        assertDoesNotThrow { GlobalEnv.LS_SEC_WEB_URL }
        assertDoesNotThrow { GlobalEnv.LS_SEC_WEB_SOCKET_URL }
        assertDoesNotThrow { GlobalEnv.LS_SEC_API_KEY }
        assertDoesNotThrow { GlobalEnv.LS_SEC_API_SECRET }
    }
}