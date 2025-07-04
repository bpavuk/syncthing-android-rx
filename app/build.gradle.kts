plugins {
    id("com.android.application")
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.lifecycle.runtime.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui.graphics)
    implementation(libs.material3)
    implementation(libs.material3.adaptive.navigation.suite)
    androidTestImplementation(libs.annotation)
    androidTestImplementation(libs.rules)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.aboutlibraries.core)
    implementation(libs.accompanist.permissions)
    implementation(libs.activity.compose)
    implementation(libs.android.material)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.constraintlayout)
    implementation(libs.core.ktx)
    implementation(libs.dagger)
    implementation(libs.documentfile)
    implementation(libs.fragment.ktx)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.jbcrypt)
    implementation(libs.libsuperuser)
    implementation(libs.lingala.zip4j)
    implementation(libs.localbroadcastmanager)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview)
    implementation(libs.stream)
    implementation(libs.volley)
    implementation(libs.zxing.android.embedded) { isTransitive = false }
    implementation(libs.zxing.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    kapt(libs.dagger.compiler)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // API
    implementation(projects.syncthingREST)
}

android {
    val ndkVersionShared = rootProject.extra.get("ndkVersionShared")
    val versionMajor: Int by rootProject.extra
    val versionMinor: Int by rootProject.extra
    val versionPatch: Int by rootProject.extra
    val versionWrapper: Int by rootProject.extra

    compileSdk = 35
    buildToolsVersion = "35.0.0"
    ndkVersion = "$ndkVersionShared"

    namespace = "com.nutomic.syncthingandroid"

    buildFeatures {
        compose = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.github.catfriend1.syncthingandroid"
        minSdk = 21
        targetSdk = 35
        versionCode = versionMajor * 1000000 + versionMinor * 10000 + versionPatch * 100 + versionWrapper
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}.${versionWrapper}"
        testApplicationId = "com.github.catfriend1.syncthingandroid.test"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = System.getenv("SYNCTHING_RELEASE_STORE_FILE")?.let(::file)
            storePassword = System.getenv("SIGNING_PASSWORD")
            keyAlias = System.getenv("SYNCTHING_RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("SIGNING_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isJniDebuggable = true
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.runCatching { getByName("release") }
                .getOrNull()
                .takeIf { it?.storeFile != null }
        }
        create("gplay") {
            initWith(getByName("release"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }


    packaging {
        jniLibs {
            // Otherwise libsyncthing.so doesn't appear where it should in installs
            // based on app bundles, and thus nothing works.
            useLegacyPackaging = true
        }
    }
    lint {
        abortOnError = true
        disable += "ExpiringTargetSdkVersion"
        disable += "ExpiredTargetSdkVersion"
    }
}

/**
 * Some languages are not supported by Google Play, so we ignore them.
 */
tasks.register<Delete>("deleteUnsupportedPlayTranslations") {
    delete(
            "src/main/play/listings/el-EL/",
            "src/main/play/listings/en/",
            "src/main/play/listings/eu/",
            "src/main/play/listings/nb/",
            "src/main/play/listings/nl_BE/",
            "src/main/play/listings/nl-BE/",
            "src/main/play/listings/nn/",
            "src/main/play/listings/ta/",
    )
}

project.afterEvaluate {
    android.buildTypes.forEach {
        val capitalizedName = it.name.replaceFirstChar { ch -> ch.uppercase() }
        tasks.named("merge${capitalizedName}JniLibFolders") {
            dependsOn(":syncthing:buildNative")
        }
    }
}
