plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.mike.divelog.bluetooth"
    compileSdk = 36

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
    implementation("io.insert-koin:koin-android:4.1.0")

    // Coroutines (https://github.com/Kotlin/kotlinx.coroutines)
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.10.2")

    // RxAndroidBle (https://github.com/dariuszseweryn/RxAndroidBle)
    implementation("com.polidea.rxandroidble3:rxandroidble:1.19.1")
}
