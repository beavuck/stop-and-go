# Contributing to Stop and Go

Thank you for considering contributing to Stop and Go!

## Opening Issues

When opening an issue, please follow the relevant template to help us assist you quickly.

### Bug Reports

[Create a bug report](https://gitlab.com/beavuck-services/stop-and-go/-/issues/new?issuable_template=bug)

Choose the `bug` issue template and provide:

- Clear steps to reproduce
- Device and Android version
- Expected vs actual behavior
- Relevant logs or screenshots

### Enhancement Requests

[Create an enhancement request](https://gitlab.com/beavuck-services/stop-and-go/-/issues/new?issuable_template=enhancement)

Choose the `enhancement` issue template and explain:

- What problem this solves
- How it would work
- Why it adds value

## Submitting Merge Requests

This project practices Test-Driven Development (TDD) and follows SOLID principles.

### Prerequisites

Before submitting an MR, ensure you understand:

- **TDD workflow**: Write failing tests first, then implementation
- **SOLID principles**: Single responsibility, dependency inversion, etc.
- **Kotlin conventions**: Immutability, composition over inheritance
- **Android best practices**: Testing strategy, accessibility, i18n

### Commit Guidelines

**Structure**: Separate your work into logical, digestible commits

**Message format**:

```
<emoji> <Action> <description>
```

Examples:

- `🐛 Fix timer not pausing when screen locked`
- `✨ Add haptic feedback for phase changes`
- `♻️ Refactor timer state management`
- `✅ Add tests for timer pause behavior`

**Rules**:

- Optional but appreciated: start with a relevant emoji (🐛 fix, ✨ feature, ♻️ refactor, ✅ test,
  etc.)
- Use imperative mood ("Add" not "Added")
- Capitalize first word
- No ending period
- Keep under 72 characters

**TDD commit order**:

1. Failing tests
2. Implementation or fix that makes tests pass

### Testing Requirements

**Unit Tests** (`app/src/test/`):

- Write tests for all core business logic
- Run: `./gradlew test jacocoTestReport`
- Coverage reports: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

**Instrumented Tests** (`app/src/androidTest/`):

- Write tests for Android-specific UI/functionality
- Run individual tests (not entire suite):
  ```bash
  ./gradlew installDebugAndroidTest
  adb shell am instrument -w -e class com.beavuck.stop_and_go.YourTest#testMethod \
    com.beavuck.stop_and_go.test/androidx.test.runner.AndroidJUnitRunner
  ```

**Important**:

- Never delete test code without approval
- Never suppress warnings without approval
- Test observable effects, not internal implementation
- Use `performScrollTo()` before assertions on potentially off-screen Compose elements

### Build Commands

**Standard build** (currently fails due to Lint bug):

```bash
./gradlew build -x lint
```

**Run tests with coverage**:

```bash
./gradlew test jacocoTestReport
```

### MR Title Format

```
#<issue> | ✨ <brief imperative description>
```

### MR Description

Choose the appropriate template (`bug_fix` or `enhancement`) and fill it out completely.

The MR title becomes a commit message on `main`, so please do make it clear and concise.
