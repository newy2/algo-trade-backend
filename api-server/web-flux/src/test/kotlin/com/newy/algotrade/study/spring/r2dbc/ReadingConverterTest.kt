package com.newy.algotrade.study.spring.r2dbc

import helpers.spring.BaseDataR2dbcTest
import io.r2dbc.spi.Row
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.annotation.Id
import org.springframework.data.convert.CustomConversions
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals

@Configuration
class ReadingConvertorR2dbcConfig {
    @Bean
    fun conversions(databaseClient: DatabaseClient): R2dbcCustomConversions {
        val dialect = DialectResolver.getDialect(databaseClient.connectionFactory)
        val converters = dialect.converters + R2dbcCustomConversions.STORE_CONVERTERS

        return R2dbcCustomConversions(
            CustomConversions.StoreConversions.of(dialect.simpleTypeHolder, converters),
            customConverters()
        )
    }

    private fun customConverters() = listOf(
        UserAndUserSnsReadingConverter(),
    )
}

@ReadingConverter
class UserAndUserSnsReadingConverter : Converter<Row, UserAndUserSns> {
    override fun convert(source: Row) = UserAndUserSns(
        userId = source["user_id"] as Long,
        email = source["email"] as String,
        snsType = source["sns_type"] as String?,
    )
}

interface UserAndUserSnsRepositoryForReadingConverterTest : CoroutineCrudRepository<UserForReadingConvertorTest, Long> {
    @Query(
        """
        SELECT u.id    as user_id
             , u.email as email
             , us.type as sns_type
        FROM   users u
        LEFT OUTER JOIN user_sns us on u.id = us.user_id
        WHERE  u.id = :userId
    """
    )
    suspend fun findByUserId(userId: Long): UserAndUserSns?
}

data class UserAndUserSns(
    val userId: Long,
    val email: String,
    val snsType: String?
)

@Table("users")
data class UserForReadingConvertorTest(
    @Id val id: Long = 0,
    val email: String,
)

@Table("user_sns")
data class UserSnsForReadingConvertorTest(
    @Id val id: Long = 0,
    val userId: Long,
    val type: String,
)

interface UserRepositoryForReadingConvertorTest : CoroutineCrudRepository<UserForReadingConvertorTest, Long>
interface UserSnsRepositoryForReadingConvertorTest : CoroutineCrudRepository<UserSnsForReadingConvertorTest, Long>

@ContextConfiguration(classes = [ReadingConvertorR2dbcConfig::class])
class ReadingConvertorTest(
    @Autowired private val userRepository: UserRepositoryForReadingConvertorTest,
    @Autowired private val userSnsRepository: UserSnsRepositoryForReadingConvertorTest,
    @Autowired private val repository: UserAndUserSnsRepositoryForReadingConverterTest,
) : BaseDataR2dbcTest() {
    @Test
    fun test() = runTransactional {
        val userId1 = userRepository.save(UserForReadingConvertorTest(email = "user1@test.com")).let { user1 ->
            userSnsRepository.save(UserSnsForReadingConvertorTest(userId = user1.id, type = "KAKAO"))
            user1.id
        }
        val userId2 = userRepository.save(UserForReadingConvertorTest(email = "user2@test.com")).id

        repository.findByUserId(userId1).let {
            assertEquals("user1@test.com", it?.email)
            assertEquals("KAKAO", it?.snsType)
        }
        repository.findByUserId(userId2).let {
            assertEquals("user2@test.com", it?.email)
            assertEquals(null, it?.snsType)
        }
    }
}