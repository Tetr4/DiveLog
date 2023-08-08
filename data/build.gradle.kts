plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.mike.divelog.data"
    compileSdk = 33

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
    // Modules
    implementation(project(":bluetooth"))

    // Dependency Injection
    implementation("io.insert-koin:koin-android:3.4.3")

    // Testing
    testImplementation("junit:junit:4.13.2")
}