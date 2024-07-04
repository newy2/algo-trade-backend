package helpers

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@DataR2dbcTest
@ContextConfiguration(classes = [TestConfig::class])
open class BaseDbTest {
    @Autowired
    private lateinit var reactiveTransactionManager: ReactiveTransactionManager

    companion object {
        private val dbms = PostgreSQLContainer(DockerImageName.parse("postgres:13.3"))

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { "r2dbc:${dbmsUrl()}" }
            registry.add("spring.r2dbc.username", dbms::getUsername)
            registry.add("spring.r2dbc.password", dbms::getPassword)
            registry.add("spring.liquibase.url") { "jdbc:${dbmsUrl()}" }
            registry.add("spring.liquibase.user", dbms::getUsername)
            registry.add("spring.liquibase.password", dbms::getPassword)
        }

        private fun dbmsUrl(): String {
            return "postgresql://${dbms.host}:${dbms.firstMappedPort}/${dbms.databaseName}"
        }

        @JvmStatic
        @BeforeAll
        internal fun setUp() {
            dbms.start()
        }
    }

    protected fun runTransactional(block: suspend () -> Unit) {
        return runBlocking {
            TransactionalOperator.create(reactiveTransactionManager).executeAndAwait {
                it.setRollbackOnly()
                block()
            }
        }
    }
}