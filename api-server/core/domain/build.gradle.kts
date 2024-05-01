plugins {
    id("kotlin-conventions")
}

dependencies {
    implementation("org.ta4j:ta4j-core:0.15")

    testImplementation(kotlin("test"))

    // TODO web-flux, ktor 모두 jackson 을 사용한다면, api 로 변경할 것
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
}