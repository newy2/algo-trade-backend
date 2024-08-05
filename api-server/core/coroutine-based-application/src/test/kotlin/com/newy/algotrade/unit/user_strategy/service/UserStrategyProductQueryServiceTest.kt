package com.newy.algotrade.unit.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyProductQueryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("단순 조회 Service - port 호출순서 확인")
class UserStrategyProductQueryServiceTest {
    private val methodCallLogs = mutableListOf<String>()
    private val service = UserStrategyProductQueryService(
        getAllUserStrategyProductPort = {
            methodCallLogs.add("getAllUserStrategyProductPort")
            emptyList()
        },
        getUserStrategyProductPort = {
            methodCallLogs.add("getUserStrategyProductPort")
            emptyList()
        },
    )

    @BeforeEach
    fun setUp() {
        methodCallLogs.clear()
    }

    @Test
    suspend fun `getAllUserStrategies 호출 확인`() {
        service.getAllUserStrategyKeys()

        assertEquals(listOf("getAllUserStrategyProductPort"), methodCallLogs)
    }

    @Test
    suspend fun `getUserStrategy 호출 확인`() {
        service.getUserStrategyKeys(1)

        assertEquals(listOf("getUserStrategyProductPort"), methodCallLogs)
    }
}