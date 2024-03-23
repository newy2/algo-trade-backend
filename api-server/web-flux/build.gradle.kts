plugins {
    id("spring-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation("io.projectreactor:reactor-test")
    implementation("io.projectreactor.tools:blockhound:1.0.7.RELEASE")

    implementation(project(":api-server:core:coroutine-based-application"))
}