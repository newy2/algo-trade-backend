package com.newy.algotrade.unit.setting.service

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.setting.domain.MarketAccount
import com.newy.algotrade.setting.domain.NotificationApp
import com.newy.algotrade.setting.domain.UserSetting
import com.newy.algotrade.setting.port.`in`.model.GetUserSettingQuery
import com.newy.algotrade.setting.port.out.GetMarketAccountsOutPort
import com.newy.algotrade.setting.port.out.GetNotificationAppOutPort
import com.newy.algotrade.setting.service.GetUserSettingQueryService
import helpers.spring.TransactionalAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSettingQueryServiceTest {
    private val query = GetUserSettingQuery(userId = 1)

    @Test
    fun `저장된 데이터가 없는 경우`() = runTest {
        val service = newService()

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
        val service = newService(
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
                    type = "SLACK",
                    url = "https://hooks.slack.com/services/1111",
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
                    type = "SLACK",
                    url = "https://hooks.slack.com/services/1111",
                ),
            ),
            service.getUserSetting(query)
        )
    }

    private fun newService(
        getMarketAccountsOutPort: GetMarketAccountsOutPort = GetMarketAccountsOutPort { emptyList() },
        getNotificationAppOutPort: GetNotificationAppOutPort = GetNotificationAppOutPort { null },
    ) = GetUserSettingQueryService(
        getMarketAccountsOutPort = getMarketAccountsOutPort,
        getNotificationAppOutPort = getNotificationAppOutPort,
    )
}

class GetUserSettingQueryServiceTransactionalAnnotationTest :
    TransactionalAnnotationTestHelper(clazz = GetUserSettingQueryService::class) {
    @Test
    fun `@Transactional 사용여부 테스트`() {
        assertTrue(hasReadOnlyTransactional(methodName = "getUserSetting"))
    }
}