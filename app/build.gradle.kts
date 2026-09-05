import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.Properties

plugins {
    id("com.android.application") version "9.2.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
    id("se.patrikerdes.use-latest-versions") version "0.2.19"
    id("com.github.ben-manes.versions") version "0.61.0"
    jacoco
}

val keystorePropertiesFile = rootProject.file(".secure_files/release-keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val javaVersion = JavaVersion.VERSION_21

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
    }
}

android {
    val packagePath = "com.beavuck.stop_and_go"
    namespace = packagePath
    compileSdk = 37
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
        // don't update manually, use the dedicated gitlab job instead ("scheduled" manual job)
        versionCode = 38
        // don't update manually, use the dedicated gitlab job instead ("scheduled" manual job)
        versionName = "1.9.8"

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
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
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
            it.maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
        checkDependencies = true
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.08.00")
    val activityVersion = "1.13.0"
    val coreKtxVersion = "1.19.0"
    val appcompatVersion = "1.8.0"
    val materialVersion = "1.14.0"
    val playReviewVersion = "2.0.2"
    val kotlinxSerializationVersion = "1.11.0"

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
    implementation("com.google.android.play:review-ktx:$playReviewVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    val junitVersion = "4.13.2"
    val mockitoVersion = "5.23.0"
    val testServicesVersion = "1.6.0"

    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestUtil("androidx.test.services:test-services:$testServicesVersion")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates Jacoco code coverage report for debug unit tests."
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val toExclude = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "**/*Constants.*",
        "**/*Config.*",
        "**/model/timer/**/*.*",
        "**/instrumented/**/*.*",
        "**/activities/**/*.*",
        "**/repositories/**/*.*",
        "**/sounds/**/*.*",
        "**/dialogs/**/*.*",
        "**/ui/**/*.*",
        "**/config/**/*.*",
        "android/**/*.*"
    )

    val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(toExclude)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

// https://github.com/ben-manes/gradle-versions-plugin
tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.register<Zip>("packageReleaseNativeDebugSymbols") {
    group = "packaging"
    description = "Packages native debug symbols for the release build into a zip file."
    dependsOn("mergeReleaseNativeLibs")

    val mergedLibsDir =
        layout.buildDirectory.dir("intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib")
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
