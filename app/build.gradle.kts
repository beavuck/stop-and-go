import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    id("com.android.application") version "8.13.2"
    id("org.jetbrains.kotlin.android") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.19"
    id("com.github.ben-manes.versions") version "0.53.0"
    jacoco
}

val keystorePropertiesFile = rootProject.file(".secure_files/release-keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

android {
    val packagePath = "com.beavuck.stop_and_go"
    namespace = packagePath
    compileSdk = 36
    ndkVersion = "27.3.13750724"

    bundle {
        language {
            enableSplit = false
            // Disable language splits, because app offers a language selection option of its own
        }
    }

    defaultConfig {
        applicationId = packagePath
        minSdk = 24
        targetSdk = 36
        // don't update manually, use the dedicated gitlab job instead ("scheduled" manual job)
        versionCode = 14
        // don't update manually, use the dedicated gitlab job instead ("scheduled" manual job)
        versionName = "1.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storeFile = keystoreProperties["storeFile"]?.let { rootProject.file(it as String) }
            storePassword = keystoreProperties["storePassword"] as String?
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            ndk {
                debugSymbolLevel = "FULL"
            }
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

    buildTypes.configureEach {
        enableUnitTestCoverage = true
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    testOptions {
        unitTests.all {
            it.jvmArgs("-XX:+EnableDynamicAgentLoading")
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
        checkDependencies = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.12.01")
    val activityVersion = "1.12.2"
    val coreKtxVersion = "1.17.0"
    val appcompatVersion = "1.7.1"
    val materialVersion = "1.13.0"

    implementation(composeBom)
    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.activity:activity:$activityVersion")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:$activityVersion")

    val junitVersion = "4.13.2"
    val mockitoVersion = "5.21.0"

    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
}

apply(from = "gradle/jacoco.gradle.kts")

tasks.register<Zip>("packageReleaseNativeDebugSymbols") {
    group = "packaging"
    description = "Packages native debug symbols for the release build into a zip file."
    dependsOn("mergeReleaseNativeLibs")

    val mergedLibsDir = layout.buildDirectory.dir("intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib")
    from(mergedLibsDir)

    archiveFileName.set("native-debug-symbols.zip")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/native-debug-symbols/release"))

    include("**/*.so")
}

afterEvaluate {
    tasks.named("bundleRelease") {
        finalizedBy("packageReleaseNativeDebugSymbols")
    }
    tasks.named("assembleRelease") {
        finalizedBy("packageReleaseNativeDebugSymbols")
    }
}
