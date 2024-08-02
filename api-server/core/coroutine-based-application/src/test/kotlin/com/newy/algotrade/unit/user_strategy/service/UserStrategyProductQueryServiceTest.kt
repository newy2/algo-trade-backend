package com.newy.algotrade.unit.user_strategy.service

import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.UserStrategyProductQueryPort
import com.newy.algotrade.coroutine_based_application.user_strategy.service.UserStrategyProductQueryService
import com.newy.algotrade.domain.user_strategy.UserStrategyKey
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@DisplayName("port 호출순서 확인")
class UserStrategyProductQueryServiceTest : NoErrorUserStrategyProductQueryAdapter() {
    private val service = UserStrategyProductQueryService(this)
    private lateinit var log: String

    @BeforeEach
    fun setUp() {
        log = ""
    }

    @Test
    suspend fun `getAllUserStrategies 호출 확인`() {
        service.getAllUserStrategyKeys()

        assertEquals("getAllUserStrategies ", log)
    }

    @Test
    suspend fun `getUserStrategy 호출 확인`() {
        service.getUserStrategyKeys(1)

        assertEquals("getUserStrategy ", log)
    }

    override suspend fun getAllUserStrategyKeys(): List<UserStrategyKey> =
        super.getAllUserStrategyKeys().also {
            log += "getAllUserStrategies "
        }

    override suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey> =
        super.getUserStrategyKeys(userStrategyId).also {
            log += "getUserStrategy "
        }
}

open class NoErrorUserStrategyProductQueryAdapter : UserStrategyProductQueryPort {
    override suspend fun getAllUserStrategyKeys(): List<UserStrategyKey> = emptyList()
    override suspend fun getUserStrategyKeys(userStrategyId: Long): List<UserStrategyKey> = emptyList()
}