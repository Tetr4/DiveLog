plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.mike.divelog.data"
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
    // Modules
    implementation(project(":persistence"))
    implementation(project(":bluetooth"))

    // Dependency Injection
    implementation("io.insert-koin:koin-android:4.0.0")

    // Paging
    api("androidx.paging:paging-runtime-ktx:3.3.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
}