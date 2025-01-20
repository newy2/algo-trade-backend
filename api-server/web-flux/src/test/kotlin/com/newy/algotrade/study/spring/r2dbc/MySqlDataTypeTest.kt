package com.newy.algotrade.study.spring.r2dbc

import helpers.getSystemProperty
import helpers.spring.BaseDataR2dbcTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import kotlin.test.assertEquals
import kotlin.test.fail


@Repository
interface UserRepositoryForCharTypeTest : CoroutineCrudRepository<UserR2dbcEntityForCharTypeTest, Long>

@Repository
interface UserRepositoryForStringTypeTest : CoroutineCrudRepository<UserR2dbcEntityForStringTypeTest, Long>

@Table("users")
data class UserR2dbcEntityForCharTypeTest(
    @Id val id: Long = 0,
    val email: String,
    val autoTradeYn: Char = 'N'
)

@Table("users")
data class UserR2dbcEntityForStringTypeTest(
    @Id val id: Long = 0,
    val email: String,
    val autoTradeYn: String = "N"
)

class MySqlDataTypeTest(
    @Autowired private val charTypeRepository: UserRepositoryForCharTypeTest,
    @Autowired private val stringTypeRepository: UserRepositoryForStringTypeTest,
) : BaseDataR2dbcTest() {
    @Test
    fun `MySql 은 CHAR(1) 타입을 Kotlin 의 Char 타입으로 변환하지 못한다`() = runTransactional {
        val dbName = getSystemProperty("X_DBMS_NAME")
        when (dbName) {
            "postgresql" -> {
                assertDoesNotThrow {
                    charTypeRepository.save(
                        UserR2dbcEntityForCharTypeTest(
                            email = "test@test.com",
                        )
                    )
                }
            }

            "mysql" -> {
                val error = assertThrows<java.lang.IllegalArgumentException> {
                    charTypeRepository.save(
                        UserR2dbcEntityForCharTypeTest(
                            email = "test@test.com",
                        )
                    )
                }
                assertEquals("Cannot encode class java.lang.Character", error.message)
            }

            else -> {
                fail("지원하지 않는 DB 입니다")
            }
        }
    }

    @Test
    fun `MySql 은 CHAR(1) 타입을 Kotlin 의 String 타입으로 변환해서 사용해야 한다`() = runTransactional {
        val dbName = getSystemProperty("X_DBMS_NAME")
        when (dbName) {
            "postgresql" -> {
                assertDoesNotThrow {
                    stringTypeRepository.save(
                        UserR2dbcEntityForStringTypeTest(
                            email = "test@test.com",
                        )
                    )
                }
            }

            "mysql" -> {
                assertDoesNotThrow {
                    stringTypeRepository.save(
                        UserR2dbcEntityForStringTypeTest(
                            email = "test@test.com",
                        )
                    )
                }
            }

            else -> {
                fail("지원하지 않는 DB 입니다")
            }
        }
    }
}