plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "cloud.mike.divelog.persistence"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        ksp {
            // See https://developer.android.com/jetpack/androidx/releases/room#compiler-options
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.generateKotlin", "true")
        }
    }

    kotlin {
        jvmToolchain(21)
    }

    testOptions {
        unitTests {
            // Required by Robolectric (https://robolectric.org/getting-started/)
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Dependency Injection
    implementation("io.insert-koin:koin-android:4.0.3")

    // Room (https://developer.android.com/jetpack/androidx/releases/room)
    val roomVersion = "2.7.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")

    // Testing
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation("androidx.test.ext:junit-ktx:1.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.robolectric:robolectric:4.14.1")
}