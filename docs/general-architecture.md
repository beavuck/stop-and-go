# General Architecture

```mermaid
classDiagram
    direction TB

    %% Activities
    class MainActivity {
        +onResume()
        +onPause()
    }

    class SettingsActivity {
        +saveSettings()
    }

    %% Core Model
    class PhaseManager {
        +getCurrentPhase()
        +advanceToNextPhase()
        +getState()
        +restoreState()
    }

    class TimerConfig
    class PhaseState
    class AppState
    class TimerConstants

    %% Repositories
    class ConfigRepository {
        +saveConfig()
        +loadConfig()
    }

    class StateRepository {
        +saveState()
        +loadState()
    }

    %% Relationships
    MainActivity --> PhaseManager : manages timer
    MainActivity --> ConfigRepository : loads config
    MainActivity --> StateRepository : persists state
    MainActivity ..> SettingsActivity : opens

    SettingsActivity --> ConfigRepository : saves config

    PhaseManager --> TimerConfig : configured by
    PhaseManager --> PhaseState : produces
    PhaseManager --> AppState : exports/imports
    PhaseManager --> TimerConstants : uses limits

    ConfigRepository --> TimerConfig : persists
    StateRepository --> AppState : persists

    TimerConfig --> TimerConstants : validates against
```

## Component Responsibilities

| Component | Role |
|-----------|------|
| **MainActivity** | Timer display, countdown execution, lifecycle handling |
| **SettingsActivity** | User input for configuration |
| **PhaseManager** | Phase state machine, growth calculation |
| **TimerConfig** | User preferences (immutable) |
| **AppState** | Runtime state snapshot for persistence |
| **PhaseState** | Current phase info for UI rendering |
| **Repositories** | SharedPreferences abstraction |
