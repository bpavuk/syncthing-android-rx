plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
}

kotlin {
    androidTarget("android")
    jvm("desktop") // Defines a JVM target for desktop applications

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Ktor client and serialization dependencies moved to commonMain
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
        val androidMain by getting {
            dependencies {
                // Android-specific dependencies can go here
            }
        }
        val desktopMain by getting {
            dependencies {
                // Desktop-specific dependencies can go here
            }
        }
    }
}

android {
    namespace = "dev.bpavuk.syncthingrest"
     compileSdk = 36
     defaultConfig {
         minSdk = 21
     }
}
