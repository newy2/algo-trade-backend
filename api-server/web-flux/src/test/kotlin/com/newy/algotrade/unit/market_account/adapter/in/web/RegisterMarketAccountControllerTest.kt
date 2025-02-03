package com.newy.algotrade.unit.market_account.adapter.`in`.web

import com.newy.algotrade.market_account.adapter.`in`.web.RegisterMarketAccountController
import com.newy.algotrade.market_account.adapter.`in`.web.model.RegisterMarketAccountRequest
import com.newy.algotrade.market_account.port.`in`.model.RegisterMarketAccountCommand
import com.newy.algotrade.spring.auth.annotation.AdminOnly
import com.newy.algotrade.spring.auth.annotation.AdminUser
import com.newy.algotrade.spring.auth.model.LoginUser
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.springframework.http.ResponseEntity
import java.net.URI
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
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

@DisplayName("Controller 에 인증 관련 애너테이션 사용 여부 확인하기")
class RegisterMarketAccountControllerAnnotationTest {
    private val methodName = "registerMarketAccount"

    @Test
    fun `sendVerifyCode 메서드는 @AdminOnly 애너테이션을 선언해야 한다`() {
        assertTrue(getMethod().hasAnnotation<AdminOnly>())
    }

    @Test
    fun `sendVerifyCode 메서드의 currentUser 파라미터는 @AdminUser 애너테이션을 선언해야 한다`() {
        val parameter = getMethod().parameters.find { it.name == "currentUser" }!!
        assertTrue(parameter.hasAnnotation<AdminUser>())
    }

    private fun getMethod() = RegisterMarketAccountController::class.functions.find { it.name == methodName }!!
}