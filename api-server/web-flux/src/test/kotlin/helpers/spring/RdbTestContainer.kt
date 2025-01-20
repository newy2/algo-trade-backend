package helpers.spring

import org.junit.jupiter.api.BeforeAll
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
open class RdbTestContainer {
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
}

private enum class DbmsType {
    POSTGRESQL {
        override fun getJdbcDatabaseContainer(): JdbcDatabaseContainer<*> =
            PostgreSQLContainer(DockerImageName.parse("postgres:16"))

    },
    MYSQL {
        override fun getJdbcDatabaseContainer(): JdbcDatabaseContainer<*> =
            MySQLContainer(DockerImageName.parse("mysql:8")).withDatabaseName("public")
    };

    abstract fun getJdbcDatabaseContainer(): JdbcDatabaseContainer<*>
    fun getDbmsJdbcUrl(databaseContainer: JdbcDatabaseContainer<*>): String =
        "${this.name.lowercase()}://${databaseContainer.host}:${databaseContainer.firstMappedPort}/${databaseContainer.databaseName}"
}