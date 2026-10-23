# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

MTB jump ("gap") calculator: the user enters jump parameters, the app draws the flight trajectory and warns about overshoot/undershoot. The repo is a Kotlin Multiplatform rewrite (branch `feature/v3-kmp`) of an Android-only app. Both platforms have all three screens (list, details with chart, edit).

## Commands

Gradle wrapper at the repo root; only `:androidGapCalc` and `:sharedGapCalc` are Gradle modules (iOS is a separate Xcode project).

```bash
./gradlew :androidGapCalc:assembleDebug        # build Android app
./gradlew :androidGapCalc:installDebug         # install on device/emulator
./gradlew :sharedGapCalc:jvmTest               # shared tests on the JVM (fastest; also runs the DB tests in jvmTest)
./gradlew :sharedGapCalc:jvmTest --tests '*UtilsTest'   # a single test class
./gradlew :sharedGapCalc:testAndroidHostTest :sharedGapCalc:macosArm64Test :sharedGapCalc:jsNodeTest   # same commonTest on the other targets
./gradlew detekt                               # lint + formatting (detekt-formatting/ktlint); config in config/detekt
./gradlew :sharedGapCalc:linkDebugFrameworkIosSimulatorArm64   # build the iOS framework by hand
./gradlew :webGapCalc:jsBrowserDevelopmentRun   # web app on http://localhost:8080 (8081 if taken); after dependency changes run kotlinUpgradeYarnLock
./gradlew :sharedGapCalc:compileKotlinJs :sharedGapCalc:compileKotlinJvm :sharedGapCalc:compileKotlinMacosArm64   # compile the non-mobile targets
```

iOS: open `iosGapCalc/iosGapCalc.xcodeproj` in Xcode (or `xcodebuild -project iosGapCalc/iosGapCalc.xcodeproj -scheme iosGapCalc -destination 'platform=iOS Simulator,name=iPhone 17 Pro' build`; needs `ANDROID_HOME` set). New Swift files must be added to `project.pbxproj` by hand/Xcode. `ENABLE_USER_SCRIPT_SANDBOXING` must stay `NO` or the Gradle phase can't write the framework. A build phase runs `./gradlew :sharedGapCalc:embedAndSignAppleFrameworkForXcode` from the repo root, so the shared Kotlin code is rebuilt on every Xcode build. The framework is named `sharedGapCalc` (`import sharedGapCalc` in Swift).

Detekt violations that existed when it was introduced are in `config/detekt/baseline.xml`; regenerate with `./gradlew detektBaseline` only deliberately. `.swiftlint.yml` exists but SwiftLint is not installed/run in CI. Android SDK path comes from `ANDROID_HOME`/`local.properties` (not committed).

Toolchain: Kotlin 1.9.24 (Compose compiler 1.5.14), lifecycle 2.8.7, AGP 8.7.3, Gradle 8.10.2 (Android Studio's sync init script needs Gradle ≥ 8.6), JVM 17, minSdk 26 / compileSdk 34, SQLDelight 2.0.0. Versions live in `gradle/libs.versions.toml`.

## Targets of `sharedGapCalc`

Android, iOS (arm64 + simulator arm64), macOS arm64, JVM and JS (browser + node). Intel Apple targets are not built because androidx.lifecycle doesn't publish them. Apps exist for Android, iOS and the web (`webGapCalc`); JVM and macOS only compile and run the shared tests, each has `*PlatformDependencies` (and driver/share service) ready for a future UI. SQLDelight is generated with `generateAsync` because the web driver is asynchronous: DB calls are suspending, `DriverFactory.createDriver()` is suspending, the sync drivers wrap the schema with `.synchronous()`. The web database is in-memory (sql.js in a worker) and is empty after a page reload.

## Localization

User-visible text lives in the platform apps, never in shared code: Android `res/values/strings.xml` + `values-ru`, iOS `en.lproj/` + `ru.lproj/Localizable.strings` (same key names on both; English is the default). Shared code exposes enums (`GapParameter`, `ParameterUnit`, `CalculationWarnings`) and numbers; `ParameterTexts.kt` (Android) and `Localization.swift` (iOS) turn them into strings. New gaps are stored with an empty title and shown as "Untitled"; the legacy Russian default title is mapped to empty in `DBSourceImpl`. Input hint icons are the drawings from the first version of the app: Android vector drawables `ic_gap` etc., iOS SVG imagesets `Hint*`.

## Architecture

Three parts; all business logic and view-models live in `sharedGapCalc`, the platform apps are thin UI shells.

**`sharedGapCalc/src/commonMain`** (package `com.github.raininforest`):
- `usecases/calculator/` — the physics: `CalculatorImpl` turns `GapParametersEntity` into a `CalculationResult` (trajectory points, landing params, warnings). `ParameterComposer` and `ShareService` are other use cases.
- `ui/details/chart/ChartBounds.kt` — data bounds and grid ticks shared by both chart renderers. `CalculatorImpl` emits the chart lines (ramp + min-R ramp with fills, finish, ±5° takeoff-angle tolerance paths, main path); styles live in `ChartConfig`, colors in `HexColors.kt` (`#RRGGBB`/`#AARRGGBB`).
- `data/repository/` — one repository per screen (list / details / edit) over `db/DBSource`; `data/entity/` holds domain/chart entities.
- `ui/<screen>/` — view-models (`GapListViewModel`, `GapDetailsViewModel`, `GapEditViewModel`) exposing `StateFlow` of sealed state classes (e.g. `GapListState`). They launch on `viewModelScope` (main) and never pick dispatchers: `DBSourceImpl` (IO) and `CalculatorImpl` (Default) are main-safe via injected dispatchers (`Dependencies`). A newer fetch cancels the previous one; never swallow `CancellationException`.
- `di/Dependencies` — hand-rolled DI container; platforms supply a `PlatformDependencies` implementation (SQL driver factory + share service). No DI framework.
- `navigation/NavDestinations` — route names/arguments shared by both platforms.
- `src/commonMain/sqldelight/.../Database.sq` — schema; generated DB class is `GapCalcDatabase`.

**Platform source sets** (`androidMain`, `appleMain` shared by iOS/macOS with `iosMain`/`macosMain` on top, `jvmMain`, `jsMain`): view-models extend the multiplatform `androidx.lifecycle.ViewModel` (exported to the iOS framework; Swift owns them and calls `viewModel.dispose()` from `util/ViewModelDisposer.kt` when the screen goes away), SQLDelight driver factories, and `PlatformDependencies` / `ShareService` implementations (iOS: `IosPlatformDependencies`, `IosShareService`, ``appleMain/util/FlowWatcher`; `ioDispatcher` is an expect/actual because js has no IO dispatcher).

**`androidGapCalc`**: Jetpack Compose UI (`ui/gap/{list,details,edit}`), one `*VMFactory` per screen that builds the shared view-model from `Dependencies`, a custom Canvas chart (`ui/gap/details/chart/ChartView.kt`). `GapCalculatorApplication` owns the `Dependencies` instance.

**`webGapCalc`**: Kotlin/JS app with plain DOM (no UI framework), hash routing (`#/`, `#/gap/<id>`, `#/gap/<id>/edit`), the same shared view-models, a Canvas chart ported from the Android one (`TrajectoryChart.kt`), its own strings (`Strings.kt`, English/Russian by browser language). `webpack.config.d/sqljs.js` copies the sql.js wasm and stubs node modules.

**`iosGapCalc`**: SwiftUI (min iOS 15, so the chart is drawn with `Path`, not Swift Charts). Kotlin `StateFlow`s are consumed from Swift through `FlowWatcher` (appleMain); `Ui/Common/KotlinInterop/Observables.swift` wraps each shared view-model in an `ObservableObject` (collects state into `@Published`, calls `dispose()` on deinit; writable `MutableStateFlow`s are set via `setValue(_:)`). `Extensions.swift` maps sealed state classes to Swift enums. `AppDependencies.shared` is the iOS equivalent of `GapCalculatorApplication.deps`.

When adding a feature, put logic and state in the shared view-model/repository first, then wire the thin Android and iOS views to it.
