package helpers.spring

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
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
@DataR2dbcTest
@ContextConfiguration(classes = [TestDbConfig::class])
open class BaseDbTest {
    @Autowired
    private lateinit var reactiveTransactionManager: ReactiveTransactionManager

    companion object {
        private val dbmsType = DbmsType.valueOf(getSystemProperty("X_DBMS_NAME").uppercase())
        private val databaseContainer: JdbcDatabaseContainer<*> = dbmsType.getJdbcDatabaseContainer()

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { "r2dbc:${dbmsUrl()}" }
            registry.add("spring.r2dbc.username", databaseContainer::getUsername)
            registry.add("spring.r2dbc.password", databaseContainer::getPassword)
            registry.add("spring.liquibase.url") { "jdbc:${dbmsUrl()}" }
            registry.add("spring.liquibase.user", databaseContainer::getUsername)
            registry.add("spring.liquibase.password", databaseContainer::getPassword)
        }

        private fun dbmsUrl(): String =
            dbmsType.getDbmsJdbcUrl(databaseContainer)

        @JvmStatic
        @BeforeAll
        internal fun setUp() {
            databaseContainer.start()
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

private enum class DbmsType {
    POSTGRESQL {
        override fun getJdbcDatabaseContainer(): JdbcDatabaseContainer<*> =
            PostgreSQLContainer(DockerImageName.parse("postgres:13.3"))

    },
    MYSQL {
        override fun getJdbcDatabaseContainer(): JdbcDatabaseContainer<*> =
            MySQLContainer(DockerImageName.parse("mysql:8")).withDatabaseName("public")
    };

    abstract fun getJdbcDatabaseContainer(): JdbcDatabaseContainer<*>
    fun getDbmsJdbcUrl(databaseContainer: JdbcDatabaseContainer<*>): String =
        "${this.name.lowercase()}://${databaseContainer.host}:${databaseContainer.firstMappedPort}/${databaseContainer.databaseName}"
}