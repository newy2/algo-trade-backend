package com.newy.algotrade.unit.setting.service

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.setting.domain.MarketAccount
import com.newy.algotrade.setting.domain.NotificationApp
import com.newy.algotrade.setting.domain.UserSetting
import com.newy.algotrade.setting.port.`in`.model.GetUserSettingQuery
import com.newy.algotrade.setting.port.out.GetMarketAccountsOutPort
import com.newy.algotrade.setting.port.out.GetNotificationAppOutPort
import com.newy.algotrade.setting.service.GetUserSettingQueryService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSettingQueryServiceTest {
    private val query = GetUserSettingQuery(userId = 1)

    @Test
    fun `저장된 데이터가 없는 경우`() = runTest {
        val service = createService()

        assertEquals(
            UserSetting(
                marketAccounts = emptyList(),
                notificationApp = null,
            ),
            service.getUserSetting(query)
        )
    }

    @Test
    fun `저장된 데이터가 있는 경우`() = runTest {
        val verifyCodeExpiredAt = LocalDateTime.now().plusMinutes(3)
        val service = createService(
            getMarketAccountsOutPort = {
                listOf(
                    MarketAccount(
                        id = 1,
                        marketCode = MarketCode.BY_BIT,
                        marketName = "바이빗",
                        displayName = "테스트 계정",
                        appKey = "APP KEY",
                        appSecret = "APP SECRET",
                    )
                )
            },
            getNotificationAppOutPort = {
                NotificationApp(
                    id = 10,
                    webhookType = "SLACK",
                    webhookUrl = "https://hooks.slack.com/services/1111",
                    isVerified = false,
                    verifyCodeExpiredAt = verifyCodeExpiredAt,
                )
            }
        )

        assertEquals(
            UserSetting(
                marketAccounts = listOf(
                    MarketAccount(
                        id = 1,
                        marketCode = MarketCode.BY_BIT,
                        marketName = "바이빗",
                        displayName = "테스트 계정",
                        appKey = "APP KEY",
                        appSecret = "APP SECRET",
                    )
                ),
                notificationApp = NotificationApp(
                    id = 10,
                    webhookType = "SLACK",
                    webhookUrl = "https://hooks.slack.com/services/1111",
                    isVerified = false,
                    verifyCodeExpiredAt = verifyCodeExpiredAt,
                ),
            ),
            service.getUserSetting(query)
        )
    }

    private fun createService(
        getMarketAccountsOutPort: GetMarketAccountsOutPort = GetMarketAccountsOutPort { emptyList() },
        getNotificationAppOutPort: GetNotificationAppOutPort = GetNotificationAppOutPort { null },
    ) = GetUserSettingQueryService(
        getMarketAccountsOutPort = getMarketAccountsOutPort,
        getNotificationAppOutPort = getNotificationAppOutPort,
    )
}

class GetUserSettingQueryServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(GetUserSettingQueryService::getUserSetting).hasReadOnlyTransactionalAnnotation())
    }
}