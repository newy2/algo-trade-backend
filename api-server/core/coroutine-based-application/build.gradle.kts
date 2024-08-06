plugins {
    id("kotlin-conventions")
}

tasks.test {
    // 테스트 JVM 에게 옵션 전달하기
    systemProperties.putAll(System.getProperties().toMap() as Map<String, String>)
}

dependencies {
    api(project(":api-server:core:domain"))
    api("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
}