plugins {
    kotlin("jvm")
    alias(libs.plugins.ktor)
    alias(libs.plugins.flyway)
    alias(libs.plugins.jooq.plugin)
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

// Отдельная конфигурация для classpath задач Flyway
val flywayMigration by configurations.creating

dependencies {
    // --- runtime сервера ---
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.hikari)
    implementation(libs.flyway.core)          // ок иметь и в runtime
    implementation(libs.jooq)
    implementation(libs.jbcrypt)
    implementation(libs.postgresql)           // JDBC-драйвер для рантайма

    // jOOQ codegen видит драйвер
    jooqGenerator(libs.postgresql)

    // >>> classpath для flyway задач: драйвер + модуль БД <<<
    add(flywayMigration.name, libs.postgresql.get().toString())
    add(flywayMigration.name, "org.flywaydb:flyway-database-postgresql:${libs.versions.flyway.get()}")
}

flyway {
    // только наша конфигурация, без compileClasspath
    configurations = arrayOf(flywayMigration.name)

    url = System.getenv("DB_URL") ?: "jdbc:postgresql://127.0.0.1:5432/univents"
    user = System.getenv("DB_USER") ?: "postgres"
    password = System.getenv("DB_PASSWORD") ?: "postgres"
}

jooq {
    version.set(libs.versions.jooq.get()) // 3.19.9
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = System.getenv("DB_URL") ?: "jdbc:postgresql://127.0.0.1:5432/univents"
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
                        isFluentSetters = true
                        isDaos = false
                        isPojos = false
                        isImmutablePojos = false
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
