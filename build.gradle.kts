

// build.gradle.kts (корень проекта)
plugins {
    // Для Android и JVM-проектов
    id("com.android.application") version "8.12.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    kotlin("jvm") version libs.versions.kotlin
}

repositories {
    google()
    mavenCentral()
}
allprojects {
    // если используешь Kotlin в подсборках
    plugins.withId("org.jetbrains.kotlin.jvm") {
        the<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>().jvmToolchain(17)
    }
}
