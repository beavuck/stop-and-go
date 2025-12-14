## Running tests

If you plan on running individual instrumented tests via terminal, first run:

```bash
./gradlew :app:installDebug :app:installAndroidTest
```

Then you can target a specific IT with:

```bash
adb shell am instrument -w -e class com.beavuck.stop_and_go.MainActivityTest#tripleTap_resetsTimerToInitialState com.beavuck.stop_and_go.test/androidx.test.runner.AndroidJUnitRunner
```

Just make sure to update the class and method names for your needs.
