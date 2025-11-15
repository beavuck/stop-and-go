import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application") version "8.13.1"
    id("org.jetbrains.kotlin.android") version "2.2.21"
    id("se.patrikerdes.use-latest-versions") version "0.2.19"
    id("com.github.ben-manes.versions") version "0.53.0"
}

android {
    namespace = "com.example.stop_and_go"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.stop_and_go"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0-alpha06")
    implementation("androidx.activity:activity:1.12.0-rc01")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
