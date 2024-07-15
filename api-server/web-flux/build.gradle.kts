plugins {
    id("spring-conventions")
}

dependencies {
    implementation(project(":api-server:core:coroutine-based-application"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.tools:blockhound:1.0.7.RELEASE")
    implementation("org.postgresql:r2dbc-postgresql")

    testImplementation(project(":ddl:liquibase"))
    testImplementation("org.liquibase:liquibase-core")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.squareup.okhttp3:mockwebserver")

    // for mysql testing
    testImplementation("org.testcontainers:mysql")
    testImplementation("com.github.jasync-sql:jasync-r2dbc-mysql:2.2.4")

    // Mac OS 버그 픽스를 위한 플러그인 (참고: https://github.com/reactor/reactor-netty/issues/2245)
    testImplementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")
}