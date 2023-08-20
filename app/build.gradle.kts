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

android {
    namespace = "cloud.mike.divelog"
    compileSdk = 33

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
        // Compose (https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    kotlin {
        // Required by AGP 8.0+
        jvmToolchain(17)
    }

    packaging {
        // Prevent collision if multiple Jar libs have the same license files
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    // Modules
    implementation(project(":data"))

    // Dependency Injection (https://insert-koin.io/)
    implementation("io.insert-koin:koin-android:3.4.3")
    implementation("io.insert-koin:koin-androidx-compose:3.4.6")

    // ViewModel Scoping (https://github.com/sebaslogen/resaca)
    implementation("com.github.sebaslogen.resaca:resaca:2.4.5")

    // AndroidX (https://developer.android.com/jetpack/androidx/versions)
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.navigation:navigation-compose:2.6.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.paging:paging-compose:3.2.0")

    // Compose (https://developer.android.com/jetpack/compose/bom/bom-mapping)
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
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
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("androidx.paging:paging-testing:3.2.0")
}
