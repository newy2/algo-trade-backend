plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenCentral()
}

group = "com.newy"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    // 테스트 JVM 에게 옵션 전달하기
    systemProperties.putAll(System.getProperties().toMap() as Map<String, String>)
}

dependencies {
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("io.projectreactor.tools:blockhound:1.0.7.RELEASE")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.glassfish.expressly:expressly:5.0.0")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.postgresql:r2dbc-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.ta4j:ta4j-core:0.15")

    testImplementation(kotlin("test"))
    testImplementation(project(":ddl:liquibase"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.liquibase:liquibase-core")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.testcontainers:testcontainers")

    // for mysql testing
    testImplementation("io.asyncer:r2dbc-mysql")
    testImplementation("org.testcontainers:mysql")

    // Mac OS 버그 픽스를 위한 플러그인 (참고: https://github.com/reactor/reactor-netty/issues/2245)
    testImplementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")
}