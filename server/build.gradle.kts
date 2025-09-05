plugins {
    application
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"   // <-- ОБЯЗАТЕЛЬНО
    id("io.ktor.plugin") version "2.3.12"

}

repositories {
    mavenCentral()
}

dependencies {

        // --- Database ---
    implementation("org.flywaydb:flyway-core:11.12.0")

    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3") // This brings in kotlinx-serialization-json
       // implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0") // You have this commented out
        // ...

    //val exposedVersion = "0.61.0" // или твоя версия, но одинакова для всех exposed-* модулей
    //implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
   // implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
   // implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    // ВОТ ЭТО НУЖНО ДЛЯ java.time:
    //implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // --- Ktor ---
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-auth:2.3.12")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-gson:2.3.12")

    // --- Database ---
    // implementation("org.jetbrains.exposed:exposed-core:0.61.1")
   // implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    //implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:7.0.2")

    // --- Logging ---
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // --- Password hashing ---
    implementation("org.mindrot:jbcrypt:0.4")

    // --- Tests ---
    testImplementation("io.ktor:ktor-server-tests:2.3.12")
    testImplementation(kotlin("test"))

}

application {
    // путь до main-файла Application.kt
    mainClass.set("com.example.univents.server.ApplicationKt")
}

kotlin { jvmToolchain(17) }
