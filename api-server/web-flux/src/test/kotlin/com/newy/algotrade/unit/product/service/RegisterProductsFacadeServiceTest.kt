package com.newy.algotrade.unit.product.service

import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.consts.ProductType
import com.newy.algotrade.product.domain.RegisterProduct
import com.newy.algotrade.product.domain.RegisterProducts
import com.newy.algotrade.product.port.`in`.model.RegisterProductResult
import com.newy.algotrade.product.service.RegisterProductsFacadeService
import helpers.spring.TransactionalAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@DisplayName("RegisterProductFacadeService 메서드 호출 순서 확인 테스트")
class RegisterProductsFacadeServiceTest {
    private val userId = 1L
    private var log: String = ""

    @Test
    fun `registerProducts 메서드는 아래와 같은 순서로 InPort 를 호출한다`() = runTest {
        val service = RegisterProductsFacadeService(
            getRegisterProductsInPort = { RegisterProducts().also { log += "getRegisterProductsInPort " } },
            setRegisterProductsInPort = { RegisterProductResult().also { log += "setRegisterProductsInPort " } },
        )
        service.registerProducts(userId)

        assertEquals("getRegisterProductsInPort setRegisterProductsInPort ", log)
    }

    @Test
    fun `getRegisterProductsInPort 의 return 값이 setRegisterProductsInPort 의 parameter 로 전달되어야 한다`() = runTest {
        var parameter: RegisterProducts? = null
        val service = RegisterProductsFacadeService(
            getRegisterProductsInPort = { RegisterProducts(listOf(createByBitProduct("BTC"))) },
            setRegisterProductsInPort = { p -> RegisterProductResult().also { parameter = p } },
        )
        service.registerProducts(userId)

        assertEquals(RegisterProducts(listOf(createByBitProduct("BTC"))), parameter)
    }

    private fun createByBitProduct(code: String): RegisterProduct {
        return RegisterProduct(
            marketCode = MarketCode.BY_BIT,
            type = ProductType.SPOT,
            code = code,
            name = code
        )
    }
}

class RegisterProductsFacadeServiceTransactionalAnnotationTest :
    TransactionalAnnotationTestHelper(clazz = RegisterProductsFacadeService::class) {
    @Test
    fun `@Transactional 애너테이션 사용 여부 테스트`() {
        assertTrue(hasNotTransactional(methodName = "registerProducts"))
    }
}