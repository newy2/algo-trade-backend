package com.newy.algotrade.study.spring.r2dbc

import helpers.diffSeconds
import helpers.spring.BaseDataR2dbcTest
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Table("users")
data class UserForAuditingTest(
    @Id val id: Long = 0,
    val email: String,
    val autoTradeYn: String = "N",
    @CreatedDate val createdAt: LocalDateTime? = null,
    @LastModifiedDate val updatedAt: LocalDateTime? = null,
)

interface UserRepositoryForAuditingTest : CoroutineCrudRepository<UserForAuditingTest, Long>

class AuditingTest(
    @Autowired private val repository: UserRepositoryForAuditingTest
) : BaseDataR2dbcTest() {
    private val initUser = UserForAuditingTest(
        email = "test@test.com",
        createdAt = null,
        updatedAt = null,
    )

    @Test
    fun `createdAt 과 updatedAt 을 null 값으로 저장하면 현재시간으로 저장된다`() = runTransactional {
        val now = LocalDateTime.now()
        val savedUser = repository.save(initUser)

        assertEquals("N", savedUser.autoTradeYn)
        assertEquals(0, diffSeconds(now, savedUser.createdAt))
        assertEquals(0, diffSeconds(now, savedUser.updatedAt))
    }

    @Test
    fun `기존 R2dbc 엔티티에 updateAt 값이 있어도 자동으로 업데이트 된다`() = runTransactional {
        val now = LocalDateTime.now()
        val beforeSavedUser = repository.save(initUser)

        delay(1000)
        assertNotNull(beforeSavedUser.updatedAt)
        val afterSavedUser = repository.save(beforeSavedUser.copy(autoTradeYn = "Y"))

        assertEquals("Y", afterSavedUser.autoTradeYn)
        assertEquals(0, diffSeconds(now, afterSavedUser.createdAt), "createdAt 은 변경 되지 않는다")
        assertEquals(1, diffSeconds(now, afterSavedUser.updatedAt))
    }

    @Test
    fun `기존 R2dbc 엔티티가 변경되지 않아도 updatedAt 이 자동으로 업데이트 된다`() = runTransactional {
        val now = LocalDateTime.now()
        val beforeSavedUser = repository.save(initUser)

        delay(1000)
        val afterSavedUser = beforeSavedUser.let { notChangeEntity ->
            repository.save(notChangeEntity)
        }

        assertEquals("N", afterSavedUser.autoTradeYn)
        assertEquals(0, diffSeconds(now, afterSavedUser.createdAt))
        assertEquals(1, diffSeconds(now, afterSavedUser.updatedAt))
    }

    @Test
    fun `updatedAt 을 명시적으로 설정해도 updatedAt 이 무시된다`() = runTransactional {
        val now = LocalDateTime.now()
        val beforeSavedUser = repository.save(initUser)

        val afterSavedUser = repository.save(beforeSavedUser.copy(updatedAt = now.plusMinutes(1)))

        assertEquals(0, diffSeconds(now, afterSavedUser.createdAt))
        assertEquals(0, diffSeconds(now, afterSavedUser.updatedAt))
    }

    @Test
    fun `createdAt 은 과거 날짜로 변경할 수 있다`() = runTransactional {
        val now = LocalDateTime.now()
        val beforeSavedUser = repository.save(initUser)

        val afterSavedUser = repository.save(beforeSavedUser.copy(createdAt = now.minusMinutes(1)))

        assertEquals(-1 * 60, diffSeconds(now, afterSavedUser.createdAt))
        assertEquals(0, diffSeconds(now, afterSavedUser.updatedAt))
    }

    @Test
    fun `createdAt 은 미래 날짜로 변경할 수 있다`() = runTransactional {
        val now = LocalDateTime.now()
        val beforeSavedUser = repository.save(initUser)

        val afterSavedUser = repository.save(beforeSavedUser.copy(createdAt = now.plusMinutes(1)))

        assertEquals(60, diffSeconds(now, afterSavedUser.createdAt))
        assertEquals(0, diffSeconds(now, afterSavedUser.updatedAt))
    }
}