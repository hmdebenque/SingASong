# Android application

Status: reboot planned; Kotlin is confirmed, no reboot scaffold exists. This directory owns the preserved legacy source, Gradle build/wrapper and future Android work. Its runtime and upgrade compatibility remain unaudited. The existing module is now the single Gradle root project; source remains in `src/`.

Owns the native phone/tablet app, Gradle wrapper/build, local metadata and migrations, reader, recordings, programme packs and Android release configuration. Existing Play identity and signing must be audited before choosing the new application ID or upgrade strategy.

Entry tasks: SAS-002/006/008/009/010 in the [backlog](../docs/project/backlog.md). Proposed UI/persistence/media choices are in the [roadmap](../ROADMAP.md). Add prerequisites, commands and actual implemented status when bootstrapping. Keep keystores and real choir files out of source control.

## Build

From the repository root:

```sh
mise run build:app
```

The task runs `./gradlew --no-daemon assembleDebug` inside this directory. Equivalent direct command: `cd app && ./gradlew --no-daemon assembleDebug`. Android Studio should open this directory. The debug APK is expected at `app/build/outputs/apk/debug/SingASong-debug.apk` relative to the repository root.

The preserved build uses Gradle 4.4, Android Gradle Plugin 3.1.2, Kotlin 1.2.30 and Android SDK platform 27. Use a compatible JDK 8 and an Android SDK with platform/build tools for this legacy plugin. Set `JAVA_HOME` to that JDK and `ANDROID_HOME` to the SDK, or configure the SDK in ignored `app/local.properties` (repository-relative). The wrapper downloads Gradle on first use; plugin/dependency resolution also requires network access. These legacy versions have not been upgraded as part of relocating the build. A successful APK build or supported modern toolchain is not claimed; SAS-006 owns modernization.

Validation on 2026-10-30: mise task validation and the `build:app` dry run passed. The actual task launched the relocated wrapper and downloaded Gradle 4.4, then failed with `Could not determine java version from '21.0.12.1'`. No APK was generated; Gradle project evaluation and compilation still require the compatible legacy JDK/SDK above.
