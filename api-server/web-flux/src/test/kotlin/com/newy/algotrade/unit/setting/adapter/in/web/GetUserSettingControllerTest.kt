package com.newy.algotrade.unit.setting.adapter.`in`.web

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.setting.adapter.`in`.web.GetUserSettingController
import com.newy.algotrade.setting.adapter.`in`.web.model.GetUserSettingResponse
import com.newy.algotrade.setting.adapter.`in`.web.model.MarketAccountDto
import com.newy.algotrade.setting.adapter.`in`.web.model.NotificationAppDto
import com.newy.algotrade.setting.domain.MarketAccount
import com.newy.algotrade.setting.domain.NotificationApp
import com.newy.algotrade.setting.domain.UserSetting
import com.newy.algotrade.setting.port.`in`.model.GetUserSettingQuery
import com.newy.algotrade.spring.auth.model.LoginUser
import com.newy.algotrade.spring.auth.model.UserRole
import helpers.spring.AdminOnlyAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetUserSettingControllerTest {
    private val fakeVerifyCodeExpiredAt = LocalDateTime.now()
    private val webRequestModel = object {
        val loginUser = LoginUser(id = 1)
    }

    @Test
    fun `Controller 는 WebRequest 모델을 InPort 모델로 변환해야 한다`() = runTest {
        lateinit var inPortModel: GetUserSettingQuery
        val controller = GetUserSettingController(
            getUserSettingInPort = { query ->
                UserSetting(
                    marketAccounts = emptyList(),
                    notificationApp = null
                ).also {
                    inPortModel = query
                }
            }
        )

        controller.getUserSetting(
            loginUser = webRequestModel.loginUser,
        )

        assertEquals(
            GetUserSettingQuery(
                userId = 1,
            ),
            inPortModel
        )
    }

    @Test
    fun `ADMIN 사용자가 조회한 경우, 데이터 마스킹 처리를 하지 않는다`() = runTest {
        val fakeUserSetting = getFakeUserSetting()
        val controller = GetUserSettingController(
            getUserSettingInPort = { fakeUserSetting.copy() }
        )

        val response = controller.getUserSetting(
            loginUser = webRequestModel.loginUser.copy(role = UserRole.ADMIN),
        )

        assertEquals(
            ResponseEntity.ok(
                GetUserSettingResponse(
                    marketAccounts = fakeUserSetting.marketAccounts.map { MarketAccountDto(it) },
                    notificationApp = NotificationAppDto(domainModel = fakeUserSetting.notificationApp!!),
                )
            ),
            response
        )
    }

    @Test
    fun `GUEST 사용자가 조회한 경우 appKey, appSecret, webhookUrl 을 마스킹 처리한다`() = runTest {
        val fakeUserSetting = getFakeUserSetting()
        val controller = GetUserSettingController(
            getUserSettingInPort = { fakeUserSetting.copy() }
        )

        val response = controller.getUserSetting(
            loginUser = webRequestModel.loginUser.copy(role = UserRole.GUEST),
        )

        assertEquals(
            ResponseEntity.ok(
                GetUserSettingResponse(
                    marketAccounts = listOf(
                        MarketAccountDto(
                            domainModel = fakeUserSetting.marketAccounts[0].copy(
                                appKey = "A*****1",
                                appSecret = "A*****1",
                            ),
                        ),
                        MarketAccountDto(
                            domainModel = fakeUserSetting.marketAccounts[1].copy(
                                appKey = "A*****2",
                                appSecret = "A*****2",
                            )
                        )
                    ),
                    notificationApp = NotificationAppDto(
                        domainModel = fakeUserSetting.notificationApp!!.copy(
                            webhookUrl = "h*****1"
                        )
                    ),
                )
            ),
            response
        )
    }

    private fun getFakeUserSetting() = UserSetting(
        marketAccounts = listOf(
            MarketAccount(
                id = 10,
                marketCode = MarketCode.BY_BIT,
                marketName = "바이빗",
                displayName = "ByBit 테스트 계정",
                appKey = "APP KEY1",
                appSecret = "APP SECRET1",
            ),
            MarketAccount(
                id = 11,
                marketCode = MarketCode.LS_SEC,
                marketName = "LS증권",
                displayName = "LS증권 테스트 계정",
                appKey = "APP KEY2",
                appSecret = "APP SECRET2",
            )
        ),
        notificationApp = NotificationApp(
            id = 100,
            webhookType = "TYPE",
            webhookUrl = "https://hooks.slack.com/services/1111",
            isVerified = false,
            verifyCodeExpiredAt = fakeVerifyCodeExpiredAt,
        ),
    )
}

class GetUserSettingControllerAnnotationTest : AdminOnlyAnnotationTestHelper(clazz = GetUserSettingController::class) {
    @Test
    fun `@AdminOnly @LoginUser 애너테이션 사용 여부 테스트`() {
        assertFalse(hasAdminOnly(methodName = "getUserSetting"))
        assertTrue(hasLoginUserInfo(methodName = "getUserSetting", parameterName = "loginUser"))
    }
}