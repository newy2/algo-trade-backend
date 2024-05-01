plugins {
    id("spring-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation(project(":api-server:core:coroutine-based-application"))
    implementation("io.projectreactor.tools:blockhound:1.0.7.RELEASE")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    // Mac OS 버그 픽스를 위한 플러그인 (참고: https://github.com/reactor/reactor-netty/issues/2245)
    testImplementation("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")
}