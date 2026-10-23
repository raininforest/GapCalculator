import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.sql.delight)
}

kotlin {
    androidLibrary {
        namespace = "com.github.raininforest"
        compileSdk = 37
        minSdk = 26
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
            }
        }
        withHostTest {}
    }

    jvm {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }

    // Intel (x64) Apple targets are not built: androidx.lifecycle doesn't publish them any more
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "sharedGapCalc"
            export(libs.androidx.lifecycle.viewmodel) // ViewModel is the super type of our view-models
        }
    }

    macosArm64()

    js {
        browser()
        nodejs()
        binaries.library()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.sqldelight.async.extensions)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        jvmMain.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
        }
        jsMain.dependencies {
            implementation(libs.sqldelight.web.worker.driver)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", libs.versions.sqlDelight.get()))
            implementation(npm("sql.js", "1.13.0"))
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
        }
        // iOS and macOS share the Apple source set
        appleMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

sqldelight {
    databases {
        create("GapCalcDatabase") {
            packageName.set("com.github.raininforest")
            // the web driver is asynchronous, so the API is generated as suspending for every platform
            generateAsync.set(true)
        }
    }
}
