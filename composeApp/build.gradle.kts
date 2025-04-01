import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            // Ktor Android Engine
            implementation("io.ktor:ktor-client-android:2.3.9")
            // Koin Android Integration (ViewModel support)
            implementation(libs.koin.android)
            implementation(libs.androidx.security.crypto)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.ktor.ktor.client.core) // Use latest Ktor version
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.ktor.serialization.kotlinx.json) // JSON serialization with Kotlinx
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json) // Use latest

            // Coroutines
            implementation(libs.kotlinx.coroutines.core) // Use latest

            // Koin for Dependency Injection
            implementation(libs.koin.core) // Core Koin library // Use latest Koin version

//            implementation(libs.navigation.compose)

            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.tab.navigator)

            implementation(libs.multiplatform.settings.no.arg)

//            implementation(libs.androidx.navigation.compose)
//            implementation(libs.koin.androidx.compose) // Koin integration for Compose
//            implementation(libs.coil.compose) // Coil for image loading
            implementation("io.coil-kt.coil3:coil-compose-core:3.1.0")
            implementation("io.coil-kt.coil3:coil-network-ktor:3.0.0-alpha08")
        }
    }
}

android {
    namespace = "org.example.medimitr"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "org.example.medimitr"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
