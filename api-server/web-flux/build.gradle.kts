plugins {
    id("spring-conventions")
    id("coroutine-conventions")
    id("domain-conventions")
}

tasks.test {
    // 테스트 JVM 에게 옵션 전달하기
    systemProperties.putAll(System.getProperties().toMap() as Map<String, String>)
}

dependencies {
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
    testImplementation("io.asyncer:r2dbc-mysql:1.2.0")

    // Mac OS 버그 픽스를 위한 플러그인 (참고: https://github.com/reactor/reactor-netty/issues/2245)
    testImplementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")
}