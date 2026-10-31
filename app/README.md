# Android application

SAS-022 supplies the modern Kotlin AndroidX Compose/Material 3 foundation. SAS-006 adds one offline practice workspace: open a PDF and recording through the system picker, navigate/zoom pages while playing/seeking audio, and restore private files/page/audio position after restart. Tap **Try generated demo** on an empty workspace to use original synthetic material. Demo drawing and tones are independent, not notation playback. The [prototype report](../docs/project/sas-006-prototype.md) records actual evidence and limits; [Android architecture](../docs/architecture/android.md) describes ownership and the VenezDiner reference.

This is a bounded technical prototype, not the full choir library. Imports are capped at 20 MB/300 pages for PDF and 64 MB for audio. Files rejected during PDF/recording validation produce an error and retain the prior import. Later page/decoder errors remain possible; validation opens the PDF and decodes its first page or prepares the recording, not every byte of every format. Password-protected PDFs have no password-entry flow. Page images are bounded to 2048 pixels per side. Playback pauses when leaving the app; cold start remains paused. Background playback, A–B loops, speed, annotations, catalogues and programmes remain future work. Real choir assets, physical-device validation and pilot workflows remain SAS-003.

All Android source, build configuration and wrapper live here. This directory is the single Gradle root project; open `app/` in Android Studio. The application ID and namespace remain `com.frenchfrie.singasong`. Local version code 1/name 0.1.0 are bootstrap values; SAS-002 must audit Console identity/signing/prior uploads before a release. There are no legacy users or migration requirements. Historical source remains in Git; see the report for retrieval paths.

## Prerequisites and setup

The root [mise configuration](../mise.toml) pins Temurin JDK `17.0.21+9` and Android command-line tools `19.0`. The [version catalog](gradle/libs.versions.toml) pins AGP `9.1.1`, matching built-in Kotlin/Compose compiler `2.2.10`, Compose BOM `2026.02.01` and Activity Compose `1.12.4`. The checksummed Gradle wrapper uses `9.3.1`. Compile/target SDK is 36; Build Tools is 36.0.0. Minimum API 26 (Android 8.0) is provisional until SAS-003 inventories pilot devices.

From the repository root:

```sh
mise install
mise run setup:app
mise run build:app
mise run lint:app
mise run check:app
```

`setup:app` installs platform 36, Build Tools 36.0.0 and platform-tools, verifies actual package files, and skips installation when present. First setup requires network access and explicit acceptance of the SDK license presented by `sdkmanager`. The tasks never pipe acceptance into the installer. SDK auto-download inside Gradle is disabled. `build:app`, `lint:app` and `check:app` depend on setup. `check:app` verifies the generated fixtures, assembles the real APK and runs Android Lint, with warnings treated as errors; only newer-version advisories are disabled because upgrades must revalidate the version matrix together.

The APK is `app/build/outputs/apk/debug/SingASong-debug.apk` relative to the repository root. Lint reports are under `app/build/reports/`. A direct Gradle command inside the mise environment is equivalent:

```sh
mise exec -- sh -c 'cd app && ./gradlew --no-daemon assembleDebug lintDebug'
```

Mise supplies `JAVA_HOME`, `ANDROID_HOME` and `ANDROID_SDK_ROOT`; no committed machine paths are needed. An existing `app/local.properties` can override SDK discovery: remove a stale `sdk.dir` or point it to the SDK shown by `mise exec -- printenv ANDROID_HOME`. IDE processes do not automatically inherit mise's environment; configure the same Gradle JDK/SDK in Android Studio. Missing tools and the Gradle distribution require downloads on first use. Only Google Maven, Maven Central and Gradle Plugin Portal are used.

## Device checks

With an authorized device or running emulator visible in `mise exec -- adb devices -l`:

```sh
mise run test:app
mise exec -- adb install -r app/build/outputs/apk/debug/SingASong-debug.apk
mise exec -- adb shell am start -W -n com.frenchfrie.singasong/.MainActivity
```

`test:app` runs nine instrumentation tests covering empty launch/recreation, real PDF/audio import/render/decoding, size/error/rollback paths, saved positions, playback/seek, backgrounding and audio-focus loss. It requires a connected target and does not manufacture a passing result when one is absent. These are device tests; there is no separate JVM unit-test suite yet. Set `ANDROID_SERIAL` or pass `adb -s SERIAL` when several targets are connected.

An optional Linux x86_64 emulator can be prepared explicitly (requires virtualization and additional disk space):

```sh
mise exec -- sdkmanager 'emulator' 'system-images;android-36;default;x86_64'
mise exec -- avdmanager create avd --name sas022-api36 --package 'system-images;android-36;default;x86_64' --device pixel_6
mise exec -- sh -c '"$ANDROID_HOME/emulator/emulator" -avd sas022-api36 -no-snapshot -gpu swiftshader'
```

Answer `no` if prompted for a custom hardware profile. Wait for full boot, unlock, then run the device checks. Do not overwrite an existing AVD with the same name. Emulator/system-image revisions are recorded in the report; they are optional validation infrastructure, not required APK build dependencies. Also check actual portrait/landscape layouts, larger/smaller windows and large font settings; the recorded emulator smoke checks do not replace pilot phone/tablet testing.

## Fixture and prototype details

Run `mise run check:fixtures` to reproduce original PDF/WAV fixture bytes and check all asset hashes. See the [fixture generator/provenance](scripts/README.md) for regeneration, MP3 encoder prerequisites and test cases. Build/test checks use committed fixtures and do not require private choir material or ffmpeg.

The app copies and validates imports before committing an internal UUID filename and versioned metadata. Source URIs are not retained, so moving/deleting the original after successful import does not break reopening. A failed replacement keeps the previous file. Platform backup remains disabled. Saved positions reflect the last completed checkpoint (approximately every 500 ms during playback and on navigation/seek/pause); abrupt termination can lose the latest fraction of a second. This is not a full backup/export design.

PDF rendering uses an unexported isolated service behind `ScoreReader`; Media3 ExoPlayer handles local recordings and audio focus. WAV and MP3 are fixture-tested; other format/codec combinations depend on platform support and are not all validated. The app has no Internet permission, account system or hosted service. Media3's transitive manifest declares network-state and wake-lock permissions; no wake mode is enabled by this foreground prototype.
