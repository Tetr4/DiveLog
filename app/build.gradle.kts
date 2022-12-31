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
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // See https://developer.android.com/jetpack/androidx/releases/compose-kotlin
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // modules
    implementation(project(":data"))

    // dependency injection
    implementation("io.insert-koin:koin-android:3.3.0")
    implementation("io.insert-koin:koin-androidx-compose:3.3.0")

    // Support
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Compose
    // See https://developer.android.com/jetpack/compose/setup#bom-version-mapping
    implementation(platform("androidx.compose:compose-bom:2022.12.00"))
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // UI
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.accompanist:accompanist-themeadapter-material:0.28.0")
    implementation("androidx.core:core-splashscreen:1.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.mockito:mockito-inline:3.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
}
