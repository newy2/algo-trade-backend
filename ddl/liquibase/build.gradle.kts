sourceSets {
    main {
        resources {
            srcDirs("./")
        }
    }
}

plugins {
    id("kotlin-conventions")
    id("org.liquibase.gradle") version "2.2.1"
}

liquibase {
    activities.register("main") {
        // TODO defaultSchemaName 변경 (스키마, 데이터베이스 자동 생성기능 확인)
        val baseArguments = mapOf(
            "searchPath" to "ddl/liquibase",
            "changelogFile" to "master_change_log.xml",
            "liquibaseSchemaName" to "liquibase",
            "defaultSchemaName" to "public",
        )

        val env = System.getenv()
        val dbArguments = mapOf(
            "mysql" to mapOf(
                "url" to env["X_MYSQL_JDBC_URL"],
                "username" to env["X_MYSQL_USERNAME"],
                "password" to env["X_MYSQL_PASSWORD"],
            ),
            "postgresql" to mapOf(
                "url" to env["X_POSTGRESQL_JDBC_URL"],
                "username" to env["X_POSTGRESQL_USERNAME"],
                "password" to env["X_POSTGRESQL_PASSWORD"],
            ),
        )

        this.arguments = baseArguments + dbArguments[env["X_DBMS_NAME"]]!!
    }
}

dependencies {
    implementation("org.liquibase:liquibase-core:4.27.0")
    implementation("org.liquibase:liquibase-gradle-plugin:2.2.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("info.picocli:picocli:4.7.5")
    add("liquibaseRuntime", "org.liquibase:liquibase-core:4.27.0")
    add("liquibaseRuntime", "org.liquibase:liquibase-gradle-plugin:2.2.1")
    add("liquibaseRuntime", "org.postgresql:postgresql:42.7.3")
    add("liquibaseRuntime", "com.mysql:mysql-connector-j:8.3.0")
    add("liquibaseRuntime", "info.picocli:picocli:4.7.5")
}