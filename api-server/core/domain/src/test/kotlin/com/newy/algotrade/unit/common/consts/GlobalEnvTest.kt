package com.newy.algotrade.unit.common.consts

import com.newy.algotrade.domain.common.consts.GlobalEnv
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class GlobalEnvTest {
    @Test
    fun `initialize 메소드를 호출하지 않은 경우`() {
        assertThrows<NoSuchElementException> { GlobalEnv.BY_BIT_WEB_URL }
        assertThrows<NoSuchElementException> { GlobalEnv.BY_BIT_WEB_SOCKET_URL }

        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_WEB_URL }
        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_WEB_SOCKET_URL }
        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_API_KEY }
        assertThrows<NoSuchElementException> { GlobalEnv.LS_SEC_API_SECRET }

        assertThrows<NoSuchElementException> { GlobalEnv.ADMIN_USER_ID }
    }

    @Test
    fun `initialize 메소드를 호출한 경우`() {
        GlobalEnv.initialize(
            byBitWebUrl = "byBitWebUrl",
            byBitWebSocketUrl = "byBitWebSocketUrl",
            lsSecWebUrl = "lsSecWebUrl",
            lsSecWebSocketUrl = "lsSecWebSocketUrl",
            lsSecApiKey = "lsSecApiKey",
            lsSecApiSecret = "lsSecApiSecret",
            adminUserId = 1
        )

        assertEquals("byBitWebUrl", GlobalEnv.BY_BIT_WEB_URL)
        assertEquals("byBitWebSocketUrl", GlobalEnv.BY_BIT_WEB_SOCKET_URL)
        assertEquals("lsSecWebUrl", GlobalEnv.LS_SEC_WEB_URL)
        assertEquals("lsSecWebSocketUrl", GlobalEnv.LS_SEC_WEB_SOCKET_URL)
        assertEquals("lsSecApiKey", GlobalEnv.LS_SEC_API_KEY)
        assertEquals("lsSecApiSecret", GlobalEnv.LS_SEC_API_SECRET)
        assertEquals(1, GlobalEnv.ADMIN_USER_ID)
    }
}