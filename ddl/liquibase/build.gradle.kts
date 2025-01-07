plugins {
    kotlin("jvm") version "2.1.0"
    id("org.liquibase.gradle") version "2.2.1"
    id("com.nocwriter.runsql") version "1.0.3"
}

repositories {
    gradlePluginPortal()
}

sourceSets {
    main {
        resources {
            srcDirs("./")
        }
    }
}

fun getDatabaseArguments(): Map<String, String> {
    return mapOf(
        "mysql" to mapOf(
            "url" to getSystemProperty("X_MYSQL_JDBC_URL"),
            "username" to getSystemProperty("X_MYSQL_USERNAME"),
            "password" to getSystemProperty("X_MYSQL_PASSWORD"),
            "driver" to "com.mysql.cj.jdbc.Driver",
        ),
        "postgresql" to mapOf(
            "url" to getSystemProperty("X_POSTGRESQL_JDBC_URL"),
            "username" to getSystemProperty("X_POSTGRESQL_USERNAME"),
            "password" to getSystemProperty("X_POSTGRESQL_PASSWORD"),
            "driver" to "org.postgresql.Driver",
        ),
    )[getSystemProperty("X_DBMS_NAME")] ?: emptyMap()
}

fun getSchemaArguments(): Map<String, String> {
    return mapOf(
        "local" to mapOf(
            "liquibaseSchemaName" to "liquibase",
            "defaultSchemaName" to "algo_trade",
        ),
        "test" to mapOf(
            "liquibaseSchemaName" to "test_liquibase",
            "defaultSchemaName" to "test_algo_trade",
        ),
        "prod" to mapOf(
            "liquibaseSchemaName" to "prod_liquibase",
            "defaultSchemaName" to "prod_algo_trade",
        ),
    )[getSystemProperty("X_APP_ENV")] ?: emptyMap()
}

fun getSystemProperty(name: String): String {
    /***
     * gradlew 의 -D 옵션을 사용하면 System.property 로 등록이 되고,
     * IntelliJ 의 gradle Enviromnet 를 사용하면 System.env 로 등록이 됨
     * --
     * 둘 다 지원하기 위해서 아래처럼 구현 함
     */
    val result = System.getProperty(name, System.getenv(name)) ?: ""
    System.setProperty(name, result) // xml 에서 property 접근하는 로직을 위해서 사용
    return result
}

liquibase {
    activities.register("main") {
        val baseArguments = mapOf(
            "searchPath" to "ddl/liquibase",
            "changelogFile" to "master_change_log.xml",
        )

        this.arguments = baseArguments + getDatabaseArguments() + getSchemaArguments()
    }
    this.runList = "main"
}

tasks.named("update").configure {
    dependsOn("createSchema")
}

// gradle 버전 마이그레이션 버그 픽스(v7 -> v8)
tasks.named("processResources").configure {
    dependsOn("compileJava", "compileKotlin")
}

task<RunSQL>("createSchema") {
    val dbArguments = getDatabaseArguments()
    val schemaArguments = getSchemaArguments()

    config {
        username = dbArguments["username"]
        password = dbArguments["password"]
        url = dbArguments["url"]
        driverClassName = dbArguments["driver"]
        script = """
            CREATE SCHEMA IF NOT EXISTS ${schemaArguments["liquibaseSchemaName"]};
            CREATE SCHEMA IF NOT EXISTS ${schemaArguments["defaultSchemaName"]};
        """.trimIndent()
    }
}

//tasks.register("createSchema", Exec::class) {
//    // TODO fix: postgres 는 실행 가능하지만, mysql 이 driver 를 로딩하지 못함
//    val dbArguments = getDatabaseArguments()
//
//    commandLine(
//        "liquibase",
//        "execute-sql",
//        "--sql-file=create_schema.sql",
//        "--url=${dbArguments["url"]}",
//        "--username=${dbArguments["username"]}",
//        "--password=${dbArguments["password"]}",
//        "--driver=${dbArguments["driver"]}"
//    )
//}

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