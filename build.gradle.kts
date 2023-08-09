import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask

plugins {
    // AGP (https://developer.android.com/build/releases/gradle-plugin)
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false

    // Kotlin (https://kotlinlang.org/docs/releases.html#release-details)
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    // detekt (https://github.com/detekt/detekt#requirements)
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

// This task will merge every module's generated detekt report
// See https://detekt.dev/docs/introduction/reporting/#merging-reports
val reportMerge by tasks.registering(ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

// Use detekt in all modules
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    detekt {
        buildUponDefaultConfig = true
        config.setFrom("${project.rootDir}/detekt.yml")
    }
    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1") // ktlint rules
    }
    tasks.withType<Detekt>().configureEach {
        finalizedBy(reportMerge)
    }
    reportMerge {
        input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
    }
}
