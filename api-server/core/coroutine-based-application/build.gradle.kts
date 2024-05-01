plugins {
    id("kotlin-conventions")
}

dependencies {
    api(project(":api-server:core:domain"))

    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
}