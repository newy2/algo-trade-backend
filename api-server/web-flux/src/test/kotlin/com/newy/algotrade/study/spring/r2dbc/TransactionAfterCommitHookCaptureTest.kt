package com.newy.algotrade.study.spring.r2dbc

import com.newy.algotrade.spring.hook.useTransactionHook
import helpers.spring.BaseDataR2dbcTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import kotlin.test.assertEquals

var log: String = ""

@Transactional
class TestService(
    private val outPort: TestOutPort,
) {
    suspend fun run() {
        useTransactionHook(onAfterCommit = {
            outPort.run()
        })
        log += "service "
    }
}

fun interface TestOutPort {
    suspend fun run()
}

@DisplayName("Output Port 가 Service 의 Transaction onAfterCommit hook 에서 실행되는지 확인하는 방법")
class TransactionAfterCommitHookCaptureTest(
    @Autowired private val reactiveTransactionManager: ReactiveTransactionManager,
) : BaseDataR2dbcTest() {
    @BeforeEach
    fun setUp() {
        log = ""
    }

    @Test
    fun `Output port 가 onAfterCommit 이후에 실행되는지 확인하기`() = runTest {
        val service = TestService(outPort = {
            log += "outPort "
        })

        TransactionalOperator.create(reactiveTransactionManager).executeAndAwait {
            useTransactionHook(
                onAfterCommit = { log += "onAfterCommit " }
            )
            service.run()
        }

        assertEquals("service onAfterCommit outPort ", log)
    }
}