# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Behavior

Banish the word "perfect" from your vocab. Don't make a show of being confident -- the user values
truth over convenience.

Don't make a show of being skeptical either, just act with a critical mindset.

When in doubt, ask for clarification rather than making assumptions.

Work in small, shippable units, unless otherwise specified. For example, when implementing a
feature, don't then simply proceed to implement other related features without asking first.
If there are issues in the first implementation, the user needs to catch them as early as possible,
to right the ship.

## Critical Rules - Get Approval First

Before taking these actions, STOP and explain the situation to the user, then let them decide:

1. **Deleting any test code**
    - When a test fails or seems problematic:
        - Explain the root cause
        - List some options (fix the test, fix the implementation, restructure, etc.)
        - Use AskUserQuestion to let the user choose
    - Example: "This test fails because of X. Options: (1) Remove test (2) Fix by doing Y (3) Change
      implementation to Z. Which would you prefer?"

2. **Suppressing any warnings**
    - Explain what the warning means and why it's appearing
    - Let the user decide whether to suppress it

3. **Making architectural decisions**
    - Choosing between different implementation approaches (e.g., StateFlow vs LiveData)
    - Changing public APIs / endpoints
    - Large refactorings not explicitly requested

4. **Deleting any existing code** (except when replacing with new implementation)
    - Removing unused functions, classes, or files
    - Let the user decide if something is truly unused

## Pre-Action Checklist

Before using Edit or Write tools, verify:

- [ ] Am I deleting test code? → Ask user first
- [ ] Am I suppressing a warning? → Ask user first
- [ ] Am I making an architectural choice? → Ask user first
- [ ] Am I about to delete code I didn't write in this session? → Explain and ask

## Coding style and practices

- Practice SOLID
    - S: Single Responsibility Principle (each class, each method, should have one reason to change)
    - O: Open/Closed Principle (classes should be open for extension but closed for modification)
    - L: Liskov Substitution Principle (subtypes must be substitutable for their base types)
    - I: Interface Segregation Principle (prefer many specific interfaces over a single general one)
    - D: Dependency Inversion Principle (depend on abstractions, not on concretions)
- Avoid boolean parameters. Boolean parameters often indicate a function is doing two different
  things. Instead, use optional parameters with meaningful types (e.g., `icon: Painter?` instead of
  `showIcon: Boolean`). This makes the code more flexible, clearer, and easier to extend.
- Do not use comments. Except if it's exceptionally useful to explain _why_ a thing is done. Never
  use comments to explain _what_ is done -- the code should read clearly on its own.
- Don't repeat yourself. If two bits of code look alike but are bound to evolve in different ways,
  fair enough, but apart from that, duplicating code or logic should be banned.
- Practice TDD: Write failing tests first. Then write code to make them pass. But don't test for
  specific behavior within a method -- test for the observable effects of that behavior. Input in,
  output out.
    - Write unit tests for all core logic. Use instrumented tests for Android-specific
      functionality.
- Use meaningful names. Choose clear and descriptive names for variables, functions, classes, and
  modules.
- Consider accessibility and internationalization from the start.
- Perform minimal changes necessary to implement features or fix bugs. Avoid large refactorings
  unless asked for.
- Never suppress any warnings -- let a human do so if they deem it necessary.
- Never delete a test -- let a human do so if they deem it necessary.
- Ensure you use no deprecated methods, APIs, or libraries.
- When implementing a feature or fixing a bug, prefer running targeted tests instead of entire
  suites
  (ask the user to run suites at their convenience).
- Follow Kotlin coding conventions and Android best practices.
- Favor immutability. Use `val` over `var` unless mutability is strictly necessary.
- Favor composition over inheritance.

## Project Overview

Stop and Go is a minimalist Android interval timer app that alternates between two full-screen
colored phases (Go/Stop) with configurable attributes. The app is written in Kotlin
using the Android SDK.

## Build & Development Commands

### Build

An Android Lint bug is currently causing build failures. To build the project while skipping
linting, use:

```bash
./gradlew build -x lint
```

### Run Tests

Run tests with coverage report

```bash
./gradlew test jacocoTestReport
```

Coverage reports are generated in:

- HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

As for instrumented tests, never run the entire suite, but do run individual ones.

To do so, run:

```bash
./gradlew installDebugAndroidTest
```

then

```bash
adb shell am instrument -w -e class com.beavuck.stop_and_go.MainActivityTest#tripleTap_resetsTimerToInitialState com.beavuck.stop_and_go.test/androidx.test.runner.AndroidJUnitRunner
```

But make sure to update the class and method names for your needs.

## Testing

The project has comprehensive test coverage:

- **Unit Tests** (`app/src/test/`): Test core business logic
- **Instrumented Tests** (`app/src/androidTest/`): Test Android UI components

`app/gradle/jacoco.gradle.kts` contains a list of files and directories excluded from unit test
coverage –
among those, many are covered by instrumented tests instead, and the files not excluded are fully
unit-tested.

### Testing Guidelines

- Never delete test code without user approval (see Critical Rules above)
- When a test fails: diagnose the root cause, propose solutions, let the user decide
- Test code should also be of high quality: let it use reusable logical units of testing code, and
  let it be clear and concise
- Compose UI tests: Use `performScrollTo()` before assertions on elements that may be off-screen
- Prefer targeted test runs over full test suites when implementing features or fixing bugs
