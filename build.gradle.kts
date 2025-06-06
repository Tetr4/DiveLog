plugins {
    // AGP (https://developer.android.com/build/releases/gradle-plugin)
    id("com.android.application") version "8.9.2" apply false
    id("com.android.library") version "8.9.2" apply false

    // Kotlin (https://kotlinlang.org/docs/releases.html#release-details)
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("com.google.devtools.ksp") version "2.1.20-2.0.0" apply false

    // Compose (https://developer.android.com/develop/ui/compose/compiler)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false

    // Detekt (https://github.com/detekt/detekt#requirements)
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

// Use detekt in all modules
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        buildUponDefaultConfig = true
        config.setFrom("${project.rootDir}/detekt.yml")
    }
    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8") // ktlint rules
    }
}
