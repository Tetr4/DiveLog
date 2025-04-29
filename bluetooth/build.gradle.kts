plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.mike.divelog.bluetooth"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    // Dependency Injection
    implementation("io.insert-koin:koin-android:4.0.3")

    // Coroutines (https://github.com/Kotlin/kotlinx.coroutines)
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.9.0")

    // RxAndroidBle (https://github.com/dariuszseweryn/RxAndroidBle)
    implementation("com.polidea.rxandroidble3:rxandroidble:1.19.0")
}
