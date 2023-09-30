plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.github.raininforest.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.github.raininforest.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":sharedGapCalc"))
    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.ui:ui-tooling:1.5.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.compose.foundation:foundation:1.5.1")
    implementation("androidx.compose.material:material:1.5.1")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.navigation:navigation-compose:2.7.2")
    implementation("com.patrykandpatrick.vico:compose:1.12.0")
}