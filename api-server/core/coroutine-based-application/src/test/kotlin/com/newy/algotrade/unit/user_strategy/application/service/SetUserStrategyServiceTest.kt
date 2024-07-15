package com.newy.algotrade.unit.user_strategy.application.service

import com.newy.algotrade.coroutine_based_application.user_strategy.application.service.SetUserStrategyService
import com.newy.algotrade.coroutine_based_application.user_strategy.domain.Product
import com.newy.algotrade.coroutine_based_application.user_strategy.port.`in`.model.SetUserStrategyCommand
import com.newy.algotrade.coroutine_based_application.user_strategy.port.out.*
import com.newy.algotrade.domain.common.consts.ProductCategory
import com.newy.algotrade.domain.common.consts.ProductType
import com.newy.algotrade.domain.common.exception.NotFoundRowException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class FakeGetMarketAdapter : GetMarketPort {
    override suspend fun getMarketIdsBy(marketAccountId: Long): List<Long> {
        return listOf(1)
    }
}

class FakeHasStrategyAdapter : HasStrategyPort {
    override suspend fun hasStrategyByClassName(strategyClassName: String): Boolean {
        return true
    }
}

class FakeGetProductAdapter : GetProductPort {
    override suspend fun getProducts(
        marketIds: List<Long>,
        productType: ProductType,
        productCodes: List<String>
    ): List<Product> {
        return productCodes.mapIndexed { index, code ->
            Product((index + 1).toLong(), code)
        }
    }
}

open class FakeUserStrategyAdapter : UserStrategyPort {
    override suspend fun setUserStrategy(
        marketServerAccountId: Long,
        strategyClassName: String,
        productType: ProductType,
        productCategory: ProductCategory
    ): Long = 1

    override suspend fun hasUserStrategy(
        marketServerAccountId: Long,
        strategyClassName: String,
        productType: ProductType
    ): Boolean = false
}

class FakeSetUserStrategyProductAdapter : SetUserStrategyProductPort {
    override suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean = true
}

@DisplayName("SetUserStrategyService 공통 제약사항 테스트")
class DefaultSetUserStrategyServiceTest {
    private val strategyCommand = SetUserStrategyCommand(
        marketAccountId = 1,
        strategyClassName = "BuyTripleRSIStrategy",
        productCategory = ProductCategory.TOP_TRADING_VALUE,
        productType = ProductType.SPOT,
        productCodes = emptyList(),
    )

    @Test
    fun `marketAccountId 가 없는 경우`() = runTest {
        class NullGetMarketAdapter : GetMarketPort {
            override suspend fun getMarketIdsBy(marketAccountId: Long): List<Long> = emptyList()
        }

        val service = SetUserStrategyService(
            NullGetMarketAdapter(),
            FakeHasStrategyAdapter(),
            FakeGetProductAdapter(),
            FakeUserStrategyAdapter(),
            FakeSetUserStrategyProductAdapter(),
        )

        try {
            service.setUserStrategy(strategyCommand)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("marketAccountId 를 찾을 수 없습니다.", e.message)
        }
    }

    @Test
    fun `strategyId 가 없는 경우`() = runTest {
        class NullHasStrategyAdapter : HasStrategyPort {
            override suspend fun hasStrategyByClassName(strategyClassName: String): Boolean = false
        }

        val service = SetUserStrategyService(
            FakeGetMarketAdapter(),
            NullHasStrategyAdapter(),
            FakeGetProductAdapter(),
            FakeUserStrategyAdapter(),
            FakeSetUserStrategyProductAdapter(),
        )

        try {
            service.setUserStrategy(strategyCommand)
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("strategyId 를 찾을 수 없습니다.", e.message)
        }
    }

    @Test
    fun `이미 등록한 userStrategy 인 경우`() = runTest {
        class AlreadyRegisteredUserStrategyAdapter : FakeUserStrategyAdapter() {
            override suspend fun hasUserStrategy(
                marketServerAccountId: Long, strategyClassName: String, productType: ProductType
            ): Boolean {
                return true
            }
        }

        val service = SetUserStrategyService(
            FakeGetMarketAdapter(),
            FakeHasStrategyAdapter(),
            FakeGetProductAdapter(),
            AlreadyRegisteredUserStrategyAdapter(),
            FakeSetUserStrategyProductAdapter(),
        )

        try {
            service.setUserStrategy(strategyCommand)
            fail()
        } catch (e: IllegalArgumentException) {
            assertEquals("이미 등록한 전략입니다.", e.message)
        }
    }
}

@DisplayName("USER_PICK 제약사항 테스트")
class UserPickSetUserStrategyServiceTest {
    @Test
    fun `DB 에 저장되지 않은 productCode 를 입력한 경우`() = runTest {
        class NullGetProductAdapter : GetProductPort {
            override suspend fun getProducts(
                marketIds: List<Long>,
                productType: ProductType,
                productCodes: List<String>
            ): List<Product> {
                return listOf(Product(1, "BTC"))
            }
        }

        val service = SetUserStrategyService(
            FakeGetMarketAdapter(),
            FakeHasStrategyAdapter(),
            NullGetProductAdapter(),
            FakeUserStrategyAdapter(),
            FakeSetUserStrategyProductAdapter(),
        )

        try {
            service.setUserStrategy(
                SetUserStrategyCommand(
                    marketAccountId = 1,
                    strategyClassName = "BuyTripleRSIStrategy",
                    productCategory = ProductCategory.USER_PICK,
                    productType = ProductType.SPOT,
                    productCodes = listOf("BTC", "ETH"),
                )
            )
            fail()
        } catch (e: NotFoundRowException) {
            assertEquals("productCode 를 찾을 수 없습니다. ([ETH])", e.message)
        }
    }
}

@DisplayName("USER_PICK 등록하기")
class UserPickProductSetUserStrategyServiceTest : FakeUserStrategyAdapter(), SetUserStrategyProductPort {
    private var log: String = ""

    @Test
    fun `전략 등록`() = runTest {
        val service = SetUserStrategyService(
            FakeGetMarketAdapter(),
            FakeHasStrategyAdapter(),
            FakeGetProductAdapter(),
            this@UserPickProductSetUserStrategyServiceTest,
            this@UserPickProductSetUserStrategyServiceTest,
        )

        service.setUserStrategy(
            SetUserStrategyCommand(
                marketAccountId = 1,
                strategyClassName = "BuyTripleRSIStrategy",
                productCategory = ProductCategory.USER_PICK,
                productType = ProductType.SPOT,
                productCodes = listOf("BTC", "ETH"),
            )
        )

        assertEquals("setUserStrategy setUserStrategyProduct ", log)
    }

    override suspend fun setUserStrategy(
        marketAccountId: Long,
        strategyClassName: String,
        productType: ProductType,
        productCategory: ProductCategory
    ): Long {
        log += "setUserStrategy "
        return 1
    }

    override suspend fun setUserStrategyProducts(userStrategyId: Long, productIds: List<Long>): Boolean {
        log += "setUserStrategyProduct "
        return true
    }
}