import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val versionCodeProp = property("version.code").toString().toInt()
val versionNameProp = property("version.name").toString()
val buildTag = System.getenv("GITHUB_RUN_NUMBER") ?: DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())!!

android {
    namespace = "cloud.mike.divelog"
    compileSdk = 35

    signingConfigs {
        create("release") {
            keyAlias = "DiveLogKey"
            keyPassword = System.getenv("SIGNING_PASSWORD") ?: ""
            storeFile = file("../keystore.jks")
            storePassword = System.getenv("SIGNING_PASSWORD") ?: ""
        }
    }

    defaultConfig {
        applicationId = "cloud.mike.divelog"
        minSdk = 26
        targetSdk = 35
        versionCode = versionCodeProp
        versionName = "$versionNameProp+$buildTag"
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

    kotlin {
        // Required by AGP and Compose
        jvmToolchain(21)
    }

    packaging {
        // Prevent collision if multiple Jar libs have the same license files
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }

    testOptions {
        // Prevent logging (e.g. Log.i) from breaking tests)
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    // Modules
    implementation(project(":data"))

    // Dependency Injection (https://insert-koin.io/)
    implementation("io.insert-koin:koin-android:4.0.3")
    implementation("io.insert-koin:koin-androidx-compose:4.0.3")

    // ViewModel Scoping (https://github.com/sebaslogen/resaca)
    implementation("io.github.sebaslogen:resaca:4.3.4")

    // AndroidX (https://developer.android.com/jetpack/androidx/versions)
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.paging:paging-compose:3.3.6")

    // Compose (https://developer.android.com/jetpack/compose/bom/bom-mapping)
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Accompanist (https://google.github.io/accompanist/)
    implementation("com.google.accompanist:accompanist-themeadapter-material3:0.30.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("androidx.paging:paging-testing:3.3.6")
}
