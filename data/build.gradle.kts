plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cloud.mike.divelog.data"
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
    // Modules
    implementation(project(":persistence"))
    implementation(project(":bluetooth"))

    // Dependency Injection
    implementation("io.insert-koin:koin-android:3.5.6")

    // Paging
    api("androidx.paging:paging-runtime-ktx:3.2.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
}