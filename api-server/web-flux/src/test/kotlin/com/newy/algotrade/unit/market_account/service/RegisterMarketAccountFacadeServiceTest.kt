package com.newy.algotrade.unit.market_account.service

import com.newy.algotrade.auth.domain.PrivateApiInfo
import com.newy.algotrade.common.consts.MarketCode
import com.newy.algotrade.common.exception.DuplicateDataException
import com.newy.algotrade.common.exception.HttpResponseException
import com.newy.algotrade.market_account.domain.MarketAccount
import com.newy.algotrade.market_account.port.`in`.model.RegisterMarketAccountCommand
import com.newy.algotrade.market_account.port.out.ExistsMarketAccountOutPort
import com.newy.algotrade.market_account.port.out.SaveMarketAccountOutPort
import com.newy.algotrade.market_account.port.out.ValidMarketAccountOutPort
import com.newy.algotrade.market_account.service.RegisterMarketAccountCommandService
import com.newy.algotrade.market_account.service.RegisterMarketAccountFacadeService
import helpers.spring.MethodAnnotationTestHelper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RegisterMarketAccountFacadeServiceTest {
    private val command = RegisterMarketAccountCommand(
        userId = 1,
        displayName = "테스트 계정",
        marketCode = "BY_BIT",
        appKey = "AppKey",
        appSecret = "AppSecret",
    )

    @Test
    fun `중복된 별명으로 등록하는 경우 에러가 발생한다`() = runTest {
        val duplicateAdapter = ExistsMarketAccountOutPort { true }
        val service = createFacadeService(
            commandService = createService(
                existsMarketAccountOutPort = duplicateAdapter,
            )
        )

        val error = assertThrows<DuplicateDataException> {
            service.registerMarketAccount(command)
        }

        assertEquals("이름 또는 appKey, appSecret 이 중복되었습니다.", error.message)
    }

    @Test
    fun `계정정보 검증에 실패한 경우 에러가 발생한다`() = runTest {
        val validationFailedAdapter = ValidMarketAccountOutPort { _, _ -> false }
        val service = createFacadeService(
            commandService = createService(
                validMarketAccountOutPort = validationFailedAdapter,
            )
        )

        val error = assertThrows<HttpResponseException> {
            service.registerMarketAccount(command)
        }

        assertEquals("거래소의 계정 정보를 조회할 수 없습니다.", error.message)
    }

    @Test
    fun `검증에 통과한 데이터는 SaveMarketAccountOutPort 으로 데이터를 저장한다`() = runTest {
        var savableMarketAccount: MarketAccount? = null
        val captureAdapter = SaveMarketAccountOutPort {
            savableMarketAccount = it
        }
        val service = createFacadeService(
            commandService = createService(
                saveMarketAccountOutPort = captureAdapter,
            )
        )

        service.registerMarketAccount(command)

        assertEquals(
            MarketAccount(
                userId = command.userId,
                displayName = command.displayName,
                marketCode = MarketCode.valueOf(command.marketCode),
                privateApiInfo = PrivateApiInfo(
                    appKey = command.appKey,
                    appSecret = command.appSecret
                )
            ),
            savableMarketAccount
        )
    }

    private fun createFacadeService(
        commandService: RegisterMarketAccountCommandService = createService()
    ) = RegisterMarketAccountFacadeService(commandService)

    private fun createService(
        existsMarketAccountOutPort: ExistsMarketAccountOutPort = ExistsMarketAccountOutPort { false },
        validMarketAccountOutPort: ValidMarketAccountOutPort = ValidMarketAccountOutPort { _, _ -> true },
        saveMarketAccountOutPort: SaveMarketAccountOutPort = SaveMarketAccountOutPort {},
    ) = RegisterMarketAccountCommandService(
        existsMarketAccountOutPort = existsMarketAccountOutPort,
        validMarketAccountOutPort = validMarketAccountOutPort,
        saveMarketAccountOutPort = saveMarketAccountOutPort,
    )
}

class RegisterMarketAccountFacadeServiceAnnotationTest {
    @Test
    fun `메서드 애너테이션 사용 여부 확인`() {
        assertTrue(MethodAnnotationTestHelper(RegisterMarketAccountFacadeService::registerMarketAccount).hasNotTransactionalAnnotation())
        assertTrue(MethodAnnotationTestHelper(RegisterMarketAccountCommandService::checkDuplicateMarketAccount).hasReadOnlyTransactionalAnnotation())
        assertTrue(MethodAnnotationTestHelper(RegisterMarketAccountCommandService::validMarketAccount).hasNotTransactionalAnnotation())
        assertTrue(MethodAnnotationTestHelper(RegisterMarketAccountCommandService::saveMarketAccount).hasWritableTransactionalAnnotation())
    }
}