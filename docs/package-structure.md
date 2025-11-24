# Package Structure

```
com.beavuck.stop_and_go/
├── activities/
│   ├── MainActivity.kt      # Timer display and execution
│   └── SettingsActivity.kt  # Configuration UI
├── model/
│   ├── TimerConstants.kt    # Limits and defaults
│   ├── TimerConfig.kt       # User configuration (immutable)
│   ├── PhaseManager.kt      # State machine for phase transitions
│   ├── PhaseState.kt        # UI-ready phase snapshot
│   └── AppState.kt          # Persistence snapshot
└── repositories/
    ├── ConfigRepository.kt  # TimerConfig ↔ SharedPreferences
    └── StateRepository.kt   # AppState ↔ SharedPreferences
```

## Package Conventions

### `model/`
Pure Kotlin with no Android dependencies (except for constants). All business logic and data classes live here. This keeps the core logic testable with fast JUnit tests.

### `activities/`
Android UI components. These orchestrate the model classes and handle lifecycle events. Tested with instrumented tests.

### `repositories/`
Data access layer. Abstracts SharedPreferences behind clean interfaces. Each repository handles one type of data (config or state).

## Adding New Features

- **New timer behavior**: Modify `PhaseManager`
- **New user setting**: Add to `TimerConfig`, update `ConfigRepository`, add UI in `SettingsActivity`
- **New runtime state**: Add to `AppState`, update `StateRepository`, handle in `MainActivity`
