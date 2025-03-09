package com.newy.algotrade.unit.product.adapter.`in`.web

import com.newy.algotrade.product.adapter.`in`.web.RegisterProductController
import com.newy.algotrade.product.adapter.`in`.web.model.RegisterProductResponse
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.ClassAnnotationTestHelper
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterProductControllerTest {
    private val webRequestModel = object {
        val loginUser = LoginUser(id = 1)
    }

    @Test
    fun `Controller 는 UserId 를 Service 로 전달한다`() = runTest {
        var inputModel: Long? = null
        val controller = RegisterProductController { userId ->
            RegisterProductResult().also {
                inputModel = userId
            }
        }
        controller.registerProducts(webRequestModel.loginUser)

        assertEquals(1, inputModel)
    }

    @Test
    fun `요청이 성공하면 200 상태를 응답한다`() = runTest {
        val controller = RegisterProductController {
            RegisterProductResult(
                savedCount = 5,
                deletedCount = 2
            )
        }
        val result = controller.registerProducts(webRequestModel.loginUser)

        assertEquals(ResponseEntity.ok(RegisterProductResponse(savedCount = 5, deletedCount = 2)), result)
    }
}

class RegisterProductControllerAnnotationTest {
    @Test
    fun `클래스 애너테이션 사용 여부 확인`() {
        val helper = ClassAnnotationTestHelper(RegisterProductController::class)
        assertTrue(helper.hasRestControllerAnnotation())
    }

    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        MethodAnnotationTestHelper(RegisterProductController::registerProducts).let {
            assertTrue(it.hasPostMappingAnnotation("/products"))
            assertTrue(it.hasAdminOnlyAnnotation())
            assertTrue(it.hasLoginUserInfoAnnotation(parameterName = "loginUser"))
        }
    }
}