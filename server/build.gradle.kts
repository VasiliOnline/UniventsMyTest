plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // --- Ktor ---
    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-auth:2.3.4")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.4")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-gson:2.3.4")

    // --- Database ---
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:7.0.2")

    // --- Logging ---
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // --- Password hashing ---
    implementation("org.mindrot:jbcrypt:0.4")

    // --- Tests ---
    testImplementation("io.ktor:ktor-server-tests:2.3.4")
    testImplementation(kotlin("test"))

}

application {
    // путь до main-файла Application.kt
    mainClass.set("com.example.univents.server.ApplicationKt")
}

//kotlin { jvmToolchain(17) }
