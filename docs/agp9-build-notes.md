# AGP 9 build config notes

Two non-obvious build settings exist to keep AGP 9 + built-in Kotlin working. Don't remove them without understanding why.

## `androidTestUtil(test-services)` in `app/build.gradle.kts`

```kotlin
androidTestUtil("androidx.test.services:test-services:<version>")
```

UTP unconditionally runs `appops set androidx.test.services MANAGE_EXTERNAL_STORAGE allow`
during instrumented-test device provisioning. Without this dependency the package is
never installed, so the grant fails with `No UID for androidx.test.services in user 0`.
Declaring it via `androidTestUtil` makes AGP install the APK → the grant succeeds.

## KGP pin in root `build.gradle.kts`

```kotlin
id("org.jetbrains.kotlin.android") version "2.4.0" apply false
```

`apply false` puts Kotlin Gradle Plugin 2.4.0 on the build classpath **without** applying
the plugin (applying it would break under `android.newDsl=true`).

Needed because AGP 9's built-in Kotlin defaults to KGP 2.2.10. The release-only task
`produceReleaseComposeMapping` then requests `org.jetbrains.kotlin:compose-group-mapping:2.2.10`,
which was never published — that artifact only exists from Kotlin 2.3.0 onward. Pinning KGP
to 2.4.0 (matching the compose compiler plugin) makes it request the published 2.4.0 artifact.

Debug builds don't run that task, so the failure only shows in release builds (CI).
