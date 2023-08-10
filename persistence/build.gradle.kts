plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "cloud.mike.divelog.persistence"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.generateKotlin", "true")
        }
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    // Dependency Injection
    implementation("io.insert-koin:koin-android:3.4.3")

    // Room (https://developer.android.com/jetpack/androidx/releases/room)
    val roomVersion = "2.6.0-alpha01" // alpha version because this supports value classes (e.g. kotlin.time.Duration)
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // TODO paging: implementation("androidx.room:room-paging:$roomVersion")

    testImplementation("androidx.room:room-testing:$roomVersion")
}