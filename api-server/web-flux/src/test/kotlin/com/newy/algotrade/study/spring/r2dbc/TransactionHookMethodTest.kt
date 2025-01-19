package com.newy.algotrade.study.spring.r2dbc

import com.newy.algotrade.spring.hook.useTransactionHook
import helpers.spring.BaseDataR2dbcTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.test.util.AssertionErrors.assertFalse
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionSynchronization
import kotlin.test.Test

var isCalledAfterCommit: Boolean = false
var afterCompletionStatus: Int = -1


@Transactional
@Component
class TransactionTestService(
    private val userRepository: TestUserRepository,
) {
    private suspend fun setTransactionHook() {
        useTransactionHook(
            onAfterCommit = { isCalledAfterCommit = true },
            onAfterCompletion = { status -> afterCompletionStatus = status }
        )
    }

    suspend fun insertUser(): Long {
        return userRepository.save(TestUserR2dbcEntity(email = "test@test.com")).id
    }

    suspend fun findUser(userId: Long): TestUserR2dbcEntity {
        return userRepository.findById(userId)!!
    }

    suspend fun deleteUser(userId: Long) {
        userRepository.deleteById(userId)
    }

    suspend fun changeUser(userId: Long, autoTradeYn: Char) {
        setTransactionHook()

        userRepository.save(userRepository.findById(userId)!!.copy(autoTradeYn = autoTradeYn))
    }

    suspend fun changeUserThrowErrorAfterTransactionHook(userId: Long, autoTradeYn: Char) {
        setTransactionHook()

        userRepository.save(userRepository.findById(userId)!!.copy(autoTradeYn = autoTradeYn))
        throw RuntimeException("ERROR")
    }

    suspend fun changeUserThrowErrorBeforeTransactionHook(userId: Long, autoTradeYn: Char) {
        userRepository.save(userRepository.findById(userId)!!.copy(autoTradeYn = autoTradeYn))
        throw RuntimeException("ERROR")

        // Transaction hook 을 호출하기 전에 Error 가 발생하면, 롤벡 이벤트를 받을 수 없다.
        setTransactionHook()
    }
}

@Repository
interface TestUserRepository : CoroutineCrudRepository<TestUserR2dbcEntity, Long>

@Table("users")
data class TestUserR2dbcEntity(
    @Id val id: Long = 0,
    val email: String,
    val autoTradeYn: Char = 'N'
)

@DisplayName("Transaction hook 메서드 테스트")
class TransactionHookMethodTest(
    @Autowired private val service: TransactionTestService,
) : BaseDataR2dbcTest() {
    var userId: Long = 0

    @BeforeEach
    fun setUp(): Unit = runBlocking {
        userId = service.insertUser()
        isCalledAfterCommit = false
        afterCompletionStatus = -1
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        service.deleteUser(userId)
    }

    @Test
    fun `User 입력 데이터 확인`() = runTest {
        val user = service.findUser(userId)
        assertEquals('N', user.autoTradeYn)
    }

    @Test
    fun `User 데이터 수정하기`() = runTest {
        assertDoesNotThrow {
            service.changeUser(userId = userId, autoTradeYn = 'Y')
        }

        val user = service.findUser(userId)
        assertEquals("커밋됨", 'Y', user.autoTradeYn)
        assertTrue("onAfterCommit hook 호출됨", isCalledAfterCommit)
        assertEquals("onAfterCompletion hook 호출됨", TransactionSynchronization.STATUS_COMMITTED, afterCompletionStatus)
    }

    @Test
    fun `transactionHook 을 호출한 이후에 Error 가 발생하면 롤벡 이벤트를 감지할 수 있다`() = runTest {
        assertThrows<RuntimeException> {
            service.changeUserThrowErrorAfterTransactionHook(userId = userId, autoTradeYn = 'Y')
        }

        val user = service.findUser(userId)
        assertEquals("롤백됨", 'N', user.autoTradeYn)
        assertFalse("onAfterCommit hook 호출 안됨", isCalledAfterCommit)
        assertEquals("onAfterCompletion hook 호출됨", TransactionSynchronization.STATUS_ROLLED_BACK, afterCompletionStatus)
    }

    @Test
    fun `transactionHook 을 호출하기 전에 Error 가 발생하면 롤벡 이벤트를 감지할 수 없다`() =
        runTest {
            assertThrows<RuntimeException> {
                service.changeUserThrowErrorBeforeTransactionHook(userId = userId, autoTradeYn = 'Y')
            }

            val user = service.findUser(userId)
            assertEquals("롤백됨", 'N', user.autoTradeYn)
            assertFalse("onAfterCommit hook 호출 안됨", isCalledAfterCommit)
            assertEquals("onAfterCompletion hook 호출 안됨", -1, afterCompletionStatus)
        }
}