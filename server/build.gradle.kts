

// Kotlin/JVM + Ktor + Flyway + jOOQ
plugins {
    id("io.ktor.plugin") version "2.3.12"
    id("org.flywaydb.flyway") version "11.12.0"
    id("nu.studer.jooq") version "10.1.1"

}

repositories {
    mavenCentral()
    google() // сам добавил
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

dependencies {


    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("org.flywaydb:flyway-core:10.16.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("org.jooq:jooq:3.20.6")
    implementation("org.mindrot:jbcrypt:0.4")

    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.21")


}

jooq {
    version.set("3.19.9")
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/univents"
                    user = System.getenv("DB_USER") ?: "postgres"
                    password = System.getenv("DB_PASSWORD") ?: "postgres"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                        isDaos = false
                        isPojos = false
                        isImmutablePojos = false
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "com.example.univents.jooq"
                        directory = "build/generated-src/jooq/main"
                    }
                }
            }
        }
    }
}
