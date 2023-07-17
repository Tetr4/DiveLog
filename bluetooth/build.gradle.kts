plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.mike.divelog.bluetooth"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Dependency Injection
    implementation("io.insert-koin:koin-android:3.4.2")

    // Coroutines + Rx
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.7.1")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // RxAndroidBle
    implementation("com.polidea.rxandroidble2:rxandroidble:1.17.0")
}
