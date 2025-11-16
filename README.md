[//]: # (TODO)
# Stop and Go

# Stop and Go

A minimalist Android interval timer that alternates between two colored phases with configurable durations and growth rates.

## What It Does

Stop and Go displays full-screen colors that alternate on a timer. Use it for interval training, pomodoro techniques, meditation timing, or any activity requiring visual time cues.

The screen alternates between:
- **Go phase** (default: green for 60 seconds)
- **Stop phase** (default: red for 15 seconds)

## Features

- Full-screen color display with customizable hex colors
- Independent duration control for each phase (1-3600 seconds)
- Growth multipliers that adjust durations after each cycle
- Cycle counter to track completed intervals
- Settings persist between sessions
- No accounts, no backend, no network required

## Configuration

### Duration Settings
- **Go Duration**: 1-3600 seconds (default: 60)
- **Stop Duration**: 1-3600 seconds (default: 15)

### Growth Multipliers
- **Go Duration Growth**: 0.01-100.0× (default: 1.0)
- **Stop Duration Growth**: 0.01-100.0× (default: 1.0)

After each complete cycle (go → stop → go), durations multiply by their growth rates. A 1.0× multiplier keeps durations constant.

**Example**: With a 1.1× go growth multiplier:
- Cycle 1: 60s go, 15s stop
- Cycle 2: 66s go, 15s stop
- Cycle 3: 73s go, 15s stop

### Colors
- **Go Color**: Hex code (default: `#20b05c`)
- **Stop Color**: Hex code (default: `#992639`)

## Building

```bash
./gradlew build
```

## Running Tests

```bash
./gradlew test
```

## Roadmap

- [ ] Keep screen awake during active timer
- [ ] Color picker UI for easier color selection
- [ ] Pause/resume functionality
- [ ] Sound/vibration notifications on phase change
