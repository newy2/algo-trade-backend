package com.newy.algotrade.unit.market_account.adapter.`in`.web

import com.newy.algotrade.market_account.adapter.`in`.web.RegisterMarketAccountController
import com.newy.algotrade.market_account.adapter.`in`.web.model.RegisterMarketAccountRequest
import com.newy.algotrade.market_account.port.`in`.model.RegisterMarketAccountCommand
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.AdminOnlyAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.springframework.http.ResponseEntity
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterMarketAccountControllerTest {
    @Test
    fun `Controller 는 WebRequest 모델을 InPort 모델로 변환해야 한다`() = runTest {
        lateinit var inPortModel: RegisterMarketAccountCommand
        val controller = RegisterMarketAccountController(
            registerMarketAccountInPort = { command ->
                inPortModel = command
            }
        )
        val webRequestModel = object {
            val loginUser = LoginUser(1.toLong())
            val request = RegisterMarketAccountRequest(
                displayName = "test",
                marketCode = "BY_BIT",
                appKey = "key",
                appSecret = "secret",
            )
        }

        controller.registerMarketAccount(
            loginUser = webRequestModel.loginUser,
            request = webRequestModel.request
        )

        assertEquals(
            RegisterMarketAccountCommand(
                userId = 1,
                displayName = "test",
                marketCode = "BY_BIT",
                appKey = "key",
                appSecret = "secret",
            ),
            inPortModel
        )
    }

    @Test
    fun `요청이 성공하면 201 상태를 응답한다`() = runTest {
        val controller = RegisterMarketAccountController(
            registerMarketAccountInPort = { }
        )
        val webRequestModel = object {
            val loginUser = LoginUser(1.toLong())
            val request = RegisterMarketAccountRequest(
                displayName = "test",
                marketCode = "BY_BIT",
                appKey = "key",
                appSecret = "secret",
            )
        }

        val response = controller.registerMarketAccount(
            loginUser = webRequestModel.loginUser,
            request = webRequestModel.request
        )

        assertEquals(
            ResponseEntity.created(URI.create("/setting")).build(),
            response
        )
    }
}

class RegisterMarketAccountControllerAnnotationTest :
    AdminOnlyAnnotationTestHelper(clazz = RegisterMarketAccountController::class) {
    @Test
    fun `@AdminOnly @LoginUser 애너테이션 사용 여부 테스트`() {
        assertTrue(hasAdminOnly(methodName = "registerMarketAccount"))
        assertTrue(hasLoginUserInfo(methodName = "registerMarketAccount", parameterName = "loginUser"))
    }
}