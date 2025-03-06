package com.newy.algotrade.unit.product.adapter.`in`.web

import com.newy.algotrade.product.adapter.`in`.web.RegisterProductController
import com.newy.algotrade.product.adapter.`in`.web.model.RegisterProductResponse
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult
import com.newy.algotrade.spring.auth.model.LoginUser
import helpers.spring.AdminOnlyAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterProductControllerTest {
    @Test
    fun `Controller 는 등록, 삭제 상품 수를 반환한다`() = runTest {
        val controller = RegisterProductController(registerProductsInPort = {
            RegisterProductResult(
                savedCount = 5,
                deletedCount = 2
            )
        })
        val webRequestModel = object {
            val loginUser = LoginUser(id = 1)
        }
        val result = controller.registerProducts(webRequestModel.loginUser)

        assertEquals(ResponseEntity.ok(RegisterProductResponse(savedCount = 5, deletedCount = 2)), result)
    }
}

class RegisterProductControllerAnnotationTest :
    AdminOnlyAnnotationTestHelper(clazz = RegisterProductController::class) {
    @Test
    fun `@AdminOnly @LoginUser 애너테이션 사용 여부 테스트`() {
        assertTrue(hasAdminOnly(methodName = "registerProducts"))
        assertTrue(hasLoginUserInfo(methodName = "registerProducts", parameterName = "loginUser"))
    }
}