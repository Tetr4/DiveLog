plugins {
    // AGP,  Kotlin and Compose (kotlinCompilerExtensionVersion) versions must match up:
    // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    // https://github.com/detekt/detekt#requirements
    id("com.android.application") version "8.0.2" apply false
    id("com.android.library") version "8.0.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false

    // See https://github.com/detekt/detekt#requirements
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
}

subprojects {
    // Use detekt in all modules
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        buildUponDefaultConfig = true
        config.setFrom("${project.rootDir}/detekt.yml")
    }
    dependencies {
        // Use ktlint rules
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
    }
}
