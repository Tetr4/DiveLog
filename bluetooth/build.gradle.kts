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
    implementation("io.insert-koin:koin-android:3.5.0")

    // Coroutines + Rx
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.7.3")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // RxAndroidBle (https://github.com/dariuszseweryn/RxAndroidBle)
    implementation("com.polidea.rxandroidble2:rxandroidble:1.17.2")
}
