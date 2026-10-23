# Releasing the Android app

## Before the first release

1. **Application id.** The app that is already on Google Play uses `gap.calculator`; this build keeps it, so it
   is published as an *update* of the same listing. (The code namespace stays `com.github.raininforest.android`.)
2. **Version.** `versionCode`/`versionName` are in `androidGapCalc/build.gradle.kts` (now `300` / `3.0.0`).
   `versionCode` must be **higher than the one that is currently published** – check *Play Console → App bundle explorer*.
3. **Signing key.** Updates must be signed with the same key as the published app. If the listing uses
   *Play App Signing* (the default for new apps), you only need your **upload key**:

   ```bash
   keytool -genkeypair -v -keystore release.jks -alias gapcalc -keyalg RSA -keysize 4096 -validity 10000
   ```

   Keep the file and the passwords somewhere safe (not in git), then copy `keystore.properties.example` to
   `keystore.properties` and fill it in. If you still have the old upload keystore, use that one.
4. **Data.** Version 2.x kept its values in its own storage; 3.0 uses a SQLite database, so jumps entered in
   an older version are not migrated.

## Build

```bash
./gradlew :androidGapCalc:bundleRelease     # app bundle for Google Play  → androidGapCalc/build/outputs/bundle/release/
./gradlew :androidGapCalc:assembleRelease   # installable apk (same signing)
./gradlew :androidGapCalc:lintRelease       # lint report: androidGapCalc/build/reports/lint-results-release.html
```

Without `keystore.properties` the release outputs are unsigned (fine for CI checks, not for upload).
The release build is minified and resource-shrunk by R8; smoke-test the *release* artefact, not only the debug one.

## Store listing checklist

* High-res icon **512×512 PNG** (the launcher icon in the repo is only 192 px) and a feature graphic 1024×500.
* Screenshots – `docs/media/` has fresh ones to start from.
* Short and full description in English and Russian.
* *Data safety*: the app collects and shares no data and requests no permissions.
* Content rating questionnaire, target audience, privacy policy URL (a one-line "no data is collected" page is enough).
* `targetSdk` is 36; Google Play requires a recent target API level, raise it in `build.gradle.kts` when asked.
