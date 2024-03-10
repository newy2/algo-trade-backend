plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.4")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
}