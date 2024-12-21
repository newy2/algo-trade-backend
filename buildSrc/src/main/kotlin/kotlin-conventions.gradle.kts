plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

group = "com.newy"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    testImplementation(kotlin("test"))
}