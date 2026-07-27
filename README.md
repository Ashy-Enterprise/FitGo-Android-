# FitGo — Android

FitGo is an Android fitness and nutrition app built with Kotlin and Jetpack Compose.

## Features

- Sleek dark-themed landing screen
- Interactive dashboard with:
  - Calorie ring and macro progress bars
  - Step and water tracking
  - Auto-generated daily workout plan
  - Weekly activity chart
  - Quick food logging
  - BMR / TDEE / BMI readouts

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel

## Build & run

```bash
export ANDROID_HOME=/path/to/Android/Sdk
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

The app installs as `com.fitgo.app`.
