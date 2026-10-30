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

The preserved build uses Gradle 4.4, Android Gradle Plugin 3.1.2, Kotlin 1.2.30 and Android SDK platform 27. The root [mise configuration](../mise.toml) provisions the required environment:

- Temurin Java 8 (`8.0.504+1`) runs Gradle; mise supplies `JAVA_HOME`.
- Android command-line tools `13.0` provide a mise-managed SDK and export `ANDROID_HOME` / `ANDROID_SDK_ROOT`.
- `setup:app` uses Temurin Java 17 (`17.0.21+9`) for `sdkmanager`, installs `platforms;android-27` and `build-tools;27.0.3`, and verifies their files exist. It skips the package installer once both are present. SDK licenses are presented interactively when needed; they are not automatically accepted.
- `build:app` depends on `setup:app`, then returns to Java 8 for Gradle. No manual Java or SDK path exports are needed for mise tasks.

To prepare the environment explicitly:

```sh
mise install --include-task-tools
mise run setup:app
mise run build:app
```

`mise run build:app` also installs missing tools and runs setup automatically. First-time setup requires network access and accepting the SDK license in the terminal. The [SDK plugin](https://github.com/jdx/vfox-android-sdk) manages environment paths; [mise Java support](https://mise.jdx.dev/lang/java.html) manages `JAVA_HOME`. Open Android Studio on `app/` and configure its Gradle JDK/SDK to match mise if building from the IDE; IDE processes do not automatically inherit task environments. An existing `app/local.properties` can override SDK discovery, so remove a stale `sdk.dir` or update it to the managed SDK shown by `mise exec -- printenv ANDROID_HOME`.

The Gradle wrapper downloads its distribution on first use. Legacy plugin/dependency repositories may impose further compatibility or availability limits; setting up tools is not proof of an APK build or the SAS-006 modern foundation.

Validation on 2026-10-30: installed all three pinned tools, confirmed `mise exec -- java -version` selects Java 8 and `mise exec -- sh -c 'cd app && ./gradlew --version'` successfully starts Gradle 4.4 on that JDK. Android SDK package installation reached its license prompt; full APK validation is pending.
