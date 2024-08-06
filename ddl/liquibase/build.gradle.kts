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

fun getSystemProperty(name: String): String {
    /***
     * gradlew 의 -D 옵션을 사용하면 System.property 로 등록이 되고,
     * IntelliJ 의 gradle Enviromnet 를 사용하면 System.env 로 등록이 됨
     * --
     * 둘 다 지원하기 위해서 아래처럼 구현 함
     */
    val result = System.getProperty(name, System.getenv(name))
    System.setProperty(name, result) // xml 에서 property 접근하는 로직을 위해서 사용
    return result
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

        val dbArguments = mapOf(
            "mysql" to mapOf(
                "url" to getSystemProperty("X_MYSQL_JDBC_URL"),
                "username" to getSystemProperty("X_MYSQL_USERNAME"),
                "password" to getSystemProperty("X_MYSQL_PASSWORD"),
            ),
            "postgresql" to mapOf(
                "url" to getSystemProperty("X_POSTGRESQL_JDBC_URL"),
                "username" to getSystemProperty("X_POSTGRESQL_USERNAME"),
                "password" to getSystemProperty("X_POSTGRESQL_PASSWORD"),
            ),
        )

        this.arguments = baseArguments + dbArguments[getSystemProperty("X_DBMS_NAME")]!!
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