package com.newy.algotrade.study.spring.r2dbc

import helpers.spring.BaseDataR2dbcTest
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

@TestConfiguration
class TestR2dbcConfig {
    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): R2dbcTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }
}

@ContextConfiguration(classes = [TestR2dbcConfig::class])
open class TransactionalRollbackTest : BaseDataR2dbcTest() {
    @Test
    @Transactional
    @Disabled
    open fun `테스트 메서드에 @Transactional 애너테이션 사용하면 IllegalStateException 에러가 발생한다`() = runTest {
        // 관련 이슈: https://github.com/spring-projects/spring-framework/issues/24226
    }
}