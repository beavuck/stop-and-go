# Stop and Go

A minimalist Android interval timer that alternates between two phases — with configurable colors,
durations, and growth rates.

## 📊 Status

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=beavuck-services_stop-and-go)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=bugs)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)

[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=beavuck-services_stop-and-go&metric=coverage)](https://sonarcloud.io/summary/new_code?id=beavuck-services_stop-and-go)

## What It Does

Stop and Go displays full-screen colors that alternate on a timer. Use it for interval training,
pomodoro techniques, meditation timing, or any activity requiring time cues.

The screen alternates between:

- **Go phase** (default: green for 60 seconds)
- **Stop phase** (default: red for 15 seconds)

## Features

- Full-screen color display with customizable hex colors
- Visual color picker with RGB sliders for easy color selection
- Independent duration control for each phase (1-3600 seconds)
- Growth multipliers that adjust durations after each cycle
- Cycle counter to track completed intervals
- Pause/resume with single tap
- Reset timer from settings
- Settings persist between sessions
- State persists across app lifecycle (rotation, backgrounding)
- No accounts, no backend, no network required

## Privacy

See [PRIVACY.md](https://gitlab.com/beavuck-services/stop-and-go/-/blob/main/PRIVACY.md?ref_type=heads)
for details.

## Gestures

- **Single tap**: Pause/resume timer
- **Triple tap**: Reset timer
- **Long press**: Open settings

## Configuration

### Growth Multipliers

After each complete cycle, durations multiply by their growth rates. A 1.0× multiplier keeps
durations constant.

**Example**: With a 1.1× go growth multiplier:

- Cycle 1: 60s go, 15s stop
- Cycle 2: 66s go, 15s stop
- Cycle 3: 73s go, 15s stop

### Colors

Colors can be configured in two ways:

- **Manual entry**: Type color names or hex color codes directly (e.g., `navy`, or `#000080`)
- **Color picker**: Click the color preview button to open an RGB slider picker with live preview

