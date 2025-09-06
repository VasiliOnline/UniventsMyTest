import org.gradle.kotlin.dsl.version

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false
// (для server модулей alias(libs.plugins.kotlin.jvm) здесь НЕ нужен, но если есть — только apply false)
}



allprojects {
    // если используешь Kotlin в подсборках
    plugins.withId("org.jetbrains.kotlin.jvm") {
        the<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>().jvmToolchain(21)
    }
}
