plugins {
    id("spring-conventions")
    kotlin("plugin.spring") version "1.8.20"
    kotlin("plugin.jpa") version "1.8.20"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("com.mysql:mysql-connector-j")

    implementation(project(":common"))
}