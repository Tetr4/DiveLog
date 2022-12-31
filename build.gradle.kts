plugins {
    // AGP,  Kotlin, Compose (kotlinCompilerExtensionVersion) and Detekt versions must match up:
    // https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    // https://github.com/detekt/detekt#requirements
    id("com.android.application") version "7.3.1" apply false
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

subprojects {
    // Use detekt in all modules
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        buildUponDefaultConfig = true
        config = files("${project.rootDir}/detekt.yml")
    }
    dependencies {
        // Use ktlint rules
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
    }
}
