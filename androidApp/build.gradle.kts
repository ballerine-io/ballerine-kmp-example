plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "io.ballerine.kmp.example.android"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("com.github.ballerine-io:ballerine-android-sdk:1.0.5")

    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    val compose_version = "1.2.1"

    implementation ("androidx.core:core-ktx:1.8.0")
    implementation ("androidx.activity:activity-compose:1.5.1")

    implementation ("androidx.compose.ui:ui:$compose_version")
    implementation ("androidx.compose.material:material:$compose_version")
    implementation ("androidx.compose.ui:ui-tooling-preview:$compose_version")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$compose_version")
    debugImplementation ("androidx.compose.ui:ui-tooling:$compose_version")

    implementation("com.google.accompanist:accompanist-permissions:0.26.0-alpha")
}