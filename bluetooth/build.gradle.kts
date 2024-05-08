plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.mike.divelog.bluetooth"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    // Dependency Injection
    implementation("io.insert-koin:koin-android:3.5.6")

    // Coroutines + Rx
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.8.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // RxAndroidBle (https://github.com/dariuszseweryn/RxAndroidBle)
    implementation("com.polidea.rxandroidble3:rxandroidble:1.18.1")
}
