# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Coding style and practices

- Do not use comments. Except if it's exceptionally useful to explain _why_ a thing is done. Never
  use comments to explain _what_is done -- the code should read clearly on its own.
- Don't repeat yourself. If two bits of code look alike but are bound to evolve in different ways,
  fair enough, but apart from that, duplicating code or logic should be banned.
- Write failing tests first. Then write code to make them pass.
- Favor immutability. Use `val` over `var` unless mutability is strictly necessary.
- Favor composition over inheritance.
- Favor small functions and classes. Each function should do one thing and do it well. Each class
  should have a single responsibility.
- Use meaningful names. Choose clear and descriptive names for variables, functions, classes, and
  modules.
- Write unit tests for all core logic. Use instrumented tests for Android-specific functionality.
- Follow Kotlin coding conventions and Android best practices.
- Perform minimal changes necessary to implement features or fix bugs. Avoid large refactorings
  unless asked for.
- Keep documentation up to date. Update README.md and files stored in ./docs/ as needed when making
  changes.

## Project Overview

Stop and Go is a minimalist Android interval timer app that alternates between two full-screen
colored phases (Go/Stop) with configurable durations and growth rates. The app is written in Kotlin
using the Android SDK.

## Build & Development Commands

### Build

```bash
./gradlew build
```

### Run Tests

```bash
# All tests
./gradlew test

# Unit tests only
./gradlew test

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Run tests with coverage report
./gradlew test jacocoTestReport
```

Coverage reports are generated in:

- HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

### Update Dependencies

```bash
./gradlew wrapper --gradle-version latest
./gradlew useLatestVersions
```

## Architecture

### Core Components

The app follows a simple state management architecture with three main classes:

1. **TimerConstants** (`TimerConstants.kt`) - Central configuration constants
    - Defines min/max duration limits (1-3600 seconds)
    - Defines min/max growth multipliers (0.01-100.0×)
    - Holds default values for durations, colors, and growth rates

2. **TimerConfig** (`TimerConfig.kt`) - Configuration data class
    - Immutable configuration for both Go and Stop phases
    - Stores: durations, growth multipliers, and hex colors
    - Contains validation logic in `validate()` method

3. **PhaseManager** (`PhaseManager.kt`) - State management
    - Manages current phase state (Go vs Stop)
    - Tracks cycle count (increments when transitioning from Stop back to Go)
    - Applies growth multipliers after each complete cycle
    - Key methods:
        - `getCurrentPhase()` - Returns current PhaseState
        - `advanceToNextPhase()` - Toggles between phases, applies growth when completing cycles
        - `reset()` - Resets to initial state

4. **MainActivity** (`MainActivity.kt`) - UI and timer management
    - Full-screen timer display with phase colors
    - CountDownTimer for phase timing
    - Gesture detection: single tap (pause/resume), long press (settings)
    - State persistence via StateRepository (survives rotation, backgrounding)

5. **SettingsActivity** (`SettingsActivity.kt`) - Configuration UI
    - Input fields for all timer settings
    - Color preview buttons with live visual feedback
    - Fragment result listeners for color picker integration
    - Save button (FAB) - saves config and clears state (resets timer)
    - Reset button (FAB) - clears state without changing config

6. **ColorPickerDialog** (`ColorPickerDialog.kt`) - Color selection UI
    - DialogFragment with RGB sliders for color selection
    - Live color preview with hex value display
    - Survives configuration changes (rotation) via saved instance state
    - Returns selected color via Fragment Result API

7. **PhaseNotificationManager** (`PhaseNotificationManager.kt`) - Notification handling
    - Creates notification channels for Go and Stop phases
    - Triggers sound/vibration on phase changes
    - Channels customizable via Settings > Notifications

8. **Repositories** - Data persistence
    - `ConfigRepository` - User settings (durations, colors, growth rates)
    - `StateRepository` - Runtime state (current phase, cycle count, remaining time)

### Phase Lifecycle

The phase cycle works as follows:

- Start: Go phase (cycle count = 0)
- After Go duration: Switch to Stop phase
- After Stop duration: Switch to Go phase AND increment cycle count AND apply growth multipliers to
  both durations
- Growth is applied using `(duration * growthMultiplier).toInt()` with bounds checking

## Technology Stack

- **Language**: Kotlin 2.2.21
- **Build Tool**: Gradle (Kotlin DSL)
- **Android SDK**: Compile SDK 36, Min SDK 24, Target SDK 36
- **JVM Target**: Java 21
- **Key Dependencies**:
    - AndroidX Core KTX
    - AppCompat
    - Fragment KTX
    - Material Components
    - ConstraintLayout

## CI/CD

The project uses GitLab CI with the following stages:

- **secure**: Secret scanning
- **test**: Unit tests and SonarQube analysis
- **update**: Automated dependency updates (scheduled)

CI runs on `alvrme/alpine-android:android-36-jdk21` Docker image.

## Testing

The project has comprehensive test coverage:

- **Unit Tests** (`app/src/test/`): Test core business logic (TimerConfig, PhaseManager)
- **Instrumented Tests** (`app/src/androidTest/`): Test Android UI components (MainActivity)

All core logic is tested. MainActivity has instrumented tests covering:

- Activity lifecycle and initialization
- Initial UI state (views, colors, labels)
- Timer countdown functionality
- Activity recreation

## To do

- [x] Implement view (basic)
- [x] Write tests for existing untested code (to prepare to switch to TDD)
- [x] Accept user input for values (durations, colors, growth rates)
- [x] Persist app state across android application lifecycle
- [x] Reset timer and cycle count
- [x] Pause/resume functionality
- [x] Keep screen awake during active timer
- [x] Color picker UI for easier color selection
- [x] Sound/vibration notifications on phase change
