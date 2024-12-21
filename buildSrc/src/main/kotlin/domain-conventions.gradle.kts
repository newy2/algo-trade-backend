plugins {
    id("kotlin-conventions")
}

dependencies {
    implementation("org.ta4j:ta4j-core:0.15")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2") // web-flux 모듈에서 사용
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish.expressly:expressly:5.0.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
}