import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.navigation.safe.args)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
}

val keyProp = Properties()
val keystore: File = rootProject.file("keystore.properties").apply {
    if (exists())
        keyProp.load(FileInputStream(this@apply))
}

val credProps = Properties()
val credentialsFile = rootProject.file("credentials.properties").apply {
    if (exists())
        credProps.load(FileInputStream(this@apply))
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "fr.skichrome.garden"
        minSdk = 21
        targetSdk = 35
        versionCode = 1004
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        if (!credentialsFile.exists())
        {
            println("CredProp don't exist")
            credProps.load(FileInputStream(File(System.getenv("API_CREDENTIAL_FILE"))))

        }
        buildConfigField("String", "API_BASE_URL", credProps["apiBaseUrl"] as String)
        buildConfigField("String", "API_KEY", credProps["apiKey"] as String)
    }

//    sourceSets {
    // Adds exported schema location as test app assets. Todo
    //androidTest.assets.srcDirs += files("${projectDir / schemas}".toString())
//    }

    signingConfigs {
        register("release") {
            if (keystore.exists())
            {
                storeFile = file(keyProp["STORE_FILE"] as String)
                storePassword = keyProp["STORE_PASS"] as String
                keyAlias = keyProp["KEY_ALIAS"] as String
                keyPassword = keyProp["KEY_PASS"] as String
            } else
            {
                storeFile = file(System.getenv("STORE_FILE"))
                storePassword = System.getenv("STORE_PASS")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASS")
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    namespace = "fr.skichrome.garden"

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview)

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation(libs.androidx.fragment.ktx)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)

    // Debug
    implementation(libs.timber)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.compose.viewmodel)
    implementation(libs.androidx.runtime.livedata)

    implementation(libs.androidx.work)

    // Dependency injection
    implementation(libs.insert.koin)
    implementation(libs.insert.koin.workmanager)

    // Retrofit & Moshi
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi.converter)
    implementation(libs.moshi)
    ksp(libs.moshi.codegen)
    implementation(libs.http.interceptor)

    // Room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Charts
    implementation(libs.sharts)
}