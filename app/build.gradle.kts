import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val versionCodeProp = property("version.code").toString().toInt()
val versionNameProp = property("version.name").toString()
val buildTag = System.getenv("GITHUB_RUN_NUMBER") ?: DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())!!

@Suppress("UnstableApiUsage") // Even basic configs are currently marked as @Incubating
android {
    namespace = "cloud.mike.divelog"
    compileSdk = 33

    signingConfigs {
        // TODO create keystore + key
        create("release") {
            keyAlias = "divelog"
            keyPassword = System.getenv("KEY_PWD") ?: ""
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("KEYSTORE_PWD") ?: ""
        }
    }

    defaultConfig {
        applicationId = "cloud.mike.divelog"
        minSdk = 26
        targetSdk = 33
        versionCode = versionCodeProp
        versionName = "$versionNameProp+$buildTag"
        archivesName.set("divelog-$versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }

    buildTypes {
        debug {
            // Set to true, if you want to debug code shrinking:
            val shrink = false
            isDebuggable = !shrink
            isMinifyEnabled = shrink
            isShrinkResources = shrink
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // See https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.4.7"
    }

    compileOptions {
        // Needed in addition to jvmToolchain because of this AGP bug: https://issuetracker.google.com/issues/260059413
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Modules
    implementation(project(":data"))

    // Dependency Injection
    implementation("io.insert-koin:koin-android:3.4.2")
    implementation("io.insert-koin:koin-androidx-compose:3.4.5")

    // ViewModel Scoping
    implementation("com.github.sebaslogen.resaca:resaca:2.4.5")

    // Support
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    // Compose
    // See https://developer.android.com/jetpack/compose/bom/bom-mapping
    implementation(platform("androidx.compose:compose-bom:2023.05.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // UI
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("com.google.accompanist:accompanist-themeadapter-material3:0.30.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.navigation:navigation-compose:2.6.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
}
