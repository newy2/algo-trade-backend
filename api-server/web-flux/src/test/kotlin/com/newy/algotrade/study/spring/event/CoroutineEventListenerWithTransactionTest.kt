package com.newy.algotrade.study.spring.event

import com.newy.algotrade.spring.hook.useTransactionHook
import com.newy.algotrade.study.spring.r2dbc.TestUserR2dbcEntity
import com.newy.algotrade.study.spring.r2dbc.TestUserRepository
import helpers.spring.BaseSpringBootTest
import kotlinx.coroutines.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import kotlin.test.assertEquals


data class CreatedUserEvent(val userId: Long)

@Transactional
@Component
class ApplicationEventTestService(
    private val eventPublisher: ApplicationEventPublisher,
    private val repository: TestUserRepository,
) {
    suspend fun insertUser(): Long {
        val userId = repository.save(TestUserR2dbcEntity(email = "user10@test.com")).id
        useTransactionHook(
            onAfterCommit = { eventPublisher.publishEvent(CreatedUserEvent(userId)) },
        )
        return userId
    }

    suspend fun findUser(userId: Long): TestUserR2dbcEntity {
        return repository.findById(userId)!!
    }

    suspend fun changeUser(userId: Long, autoTradeYn: String) {
        repository.save(repository.findById(userId)!!.copy(autoTradeYn = autoTradeYn))
    }

    suspend fun deleteUser(userId: Long) {
        repository.deleteById(userId)
    }
}

@Component
class ApplicationEventTestListener(
    private val service: ApplicationEventTestService,
) {
    @Transactional
    suspend fun changeUser(userId: Long) {
        service.changeUser(userId, "Y")
    }

    @EventListener
    fun onCreatedUserEvent(event: CreatedUserEvent) {
        CoroutineScope(Dispatchers.IO).launch {
            changeUser(event.userId)
        }
    }
}

@DisplayName("코루틴 기반 비동기 이벤트 리스너 DB 접근 테스트")
class AsyncApplicationEventWithTransactionTest(
    @Autowired private val service: ApplicationEventTestService,
) : BaseSpringBootTest() {
    @Test
    fun `이벤트 리스너에서 User 데이터 수정하기`(): Unit = runBlocking {
        val userId = service.insertUser()
        service.findUser(userId).let { beforeUser ->
            assertEquals("user10@test.com", beforeUser.email)
            assertEquals("N", beforeUser.autoTradeYn)
        }

        delay(200) // wait for async listener

        service.findUser(userId).let { afterUser ->
            assertEquals("user10@test.com", afterUser.email)
            assertEquals("Y", afterUser.autoTradeYn)
        }

        service.deleteUser(userId)
    }
}