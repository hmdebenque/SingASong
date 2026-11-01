# SAS-022 Android modernization evidence

Historical SAS-022 foundation snapshot; the subsequent [SAS-006 prototype](sas-006-prototype.md) extends the active app. Implementation session: 2026-10-30–31. Owner: OpenAI GPT-6.1 Sol, high reasoning, session `term_8a2a5436-6493-4a8f-9723-bb92bc1b97db`. Status and assignment are authoritative in the [backlog](backlog.md); scope is the [SAS-022 specification](sas-022-android-modernization.md).

## Delivered behavior

A minimal Kotlin AndroidX Compose/Material 3 app builds through root mise tasks and launches an empty choir library. It supports system light/dark mode, rotation, wide windows and scrolling with large text. Application ID/namespace stays `com.frenchfrie.singasong`; all Android build files and the wrapper stay under `app/`. The single Gradle root project remains named `SingASong`.

The maintainer authorized replacing active legacy framework code and completing local JDK/SDK/emulator setup without further routine approvals. Existing uncommitted planning changes were incorporated in this checkout on `sas-001-planning-tracker`. History was not rewritten. No legacy installed-user migration is needed: the app was never published and has no users.

## Verified version matrix

These are deliberate stable API 36 foundation pins, not a claim to use every newest release. The catalog, wrapper and mise setup were validated together. Newer-version lint advisories are disabled individually (`AndroidGradlePluginVersion`, `GradleDependency`, `NewerVersionAvailable`); all other lint checks remain enabled, including optional checks, with warnings treated as errors. Upgrade the matrix together and rerun build/device checks.

| Component | Pin / actual result | Compatibility evidence |
| --- | --- | --- |
| AGP | 9.1.1 | [Official 9.1 compatibility table](https://developer.android.com/build/releases/agp-9-1-0-release-notes): Gradle 9.3.1, JDK 17, Build Tools 36.0.0, built-in KGP 2.2.10; supports API 37 and below |
| Gradle | 9.3.1, binary distribution | [Gradle compatibility](https://docs.gradle.org/9.3.1/userguide/compatibility.html): runs on JDK 17–25; regenerated all wrapper files with 9.3.1; component Git attributes preserve LF/CRLF script conventions |
| JDK | Temurin 17.0.21+9 | `mise exec -- java -version` verified; one JDK runs Gradle, Kotlin and SDK tools |
| Kotlin / Compose compiler plugin | 2.2.10 / 2.2.10 | [Built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin), [Compose compiler setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler), and [AGP published POM](https://dl.google.com/dl/android/maven2/com/android/tools/build/gradle/9.1.1/gradle-9.1.1.pom) agree; no `kotlin-android` plugin applied |
| Compose BOM | 2026.02.01 | [Official BOM guidance](https://developer.android.com/develop/ui/compose/bom) and [published BOM POM](https://dl.google.com/dl/android/maven2/androidx/compose/compose-bom/2026.02.01/compose-bom-2026.02.01.pom); compiler pin is separate |
| Resolved Compose UI / Foundation / Runtime | 1.10.4 | BOM-managed; [stable UI release](https://developer.android.com/jetpack/androidx/releases/compose-ui#1.10.4); actual runtime dependency report agrees |
| Resolved Material 3 | 1.4.0 | BOM-managed; [stable Material 3 releases](https://developer.android.com/jetpack/androidx/releases/compose-material3#1.4.0) |
| Activity Compose | 1.12.4 | [Stable release notes](https://developer.android.com/jetpack/androidx/releases/activity#1.12.4); real launch/recreation tests passed |
| AndroidX Test JUnit / runner | 1.3.0 / 1.7.0 | [Official test releases](https://developer.android.com/jetpack/androidx/releases/test); Compose test libraries align with the same BOM |
| Compile / target SDK | 36 / 36 | AGP-supported; [Play phone/tablet submission floor](https://developer.android.com/google/play/requirements/target-sdk) checked during implementation is API 36; recheck before publication |
| Minimum SDK | 26 (Android 8.0), provisional | Broad older-device baseline; above the selected AndroidX minimum 23. API 26 was actually tested. SAS-003 must confirm pilot-device needs; this is not a renderer compatibility decision |
| SDK command-line tools | 19.0, archive build 13114758 | mise-managed `sdkmanager` / `avdmanager`, verified with JDK 17 |
| SDK platform / Build Tools | android-36 revision 2 / 36.0.0 | Installed and inspected with `sdkmanager --list_installed`; setup checks real platform/tool files |
| Local device tooling | platform-tools 37.0.1 / emulator 37.2.13 | Recorded installed stable revisions; SDK package names for these tools follow Google's stable channel, so future installs can receive newer revisions |
| Local system images | API 36 default x86_64 rev 2; API 26 default x86_64 rev 1 | Installed without Play/account requirements; these optional packages are not APK build dependencies |

No preview, dynamic or snapshot versions are declared in the application catalog. Gradle's displayed embedded Kotlin 2.2.21 is its Kotlin DSL runtime; the app compiler and resolved Kotlin stdlib are 2.2.10. Java/Kotlin bytecode targets are 17. No NDK or app-owned native code is needed; Compose transitively packages `libandroidx.graphics.path.so` and the build reports it is packaged without symbol stripping.

Distribution SHA-256, enforced by the wrapper: `b266d5ff6b90eada6dc3b20cb090e3731302e553a27c5d3e4df1f0d76beaff06`. Wrapper JAR SHA-256: `b3a875ddc1f044746e1b1a55f645584505f4a10438c1afea9f15e92a7c42ec13`, verified against Gradle's [published JAR checksum](https://services.gradle.org/distributions/gradle-9.3.1-wrapper.jar.sha256).

## Source and dependency disposition

| Historical input | Disposition |
| --- | --- |
| Kotlin 1.2.30, AGP 3.1.2, Gradle 4.4, SDK 27 and Java 8 split setup | Replaced by the matrix above; Kotlin DSL and a version catalog replace Groovy build files |
| Android support AppCompat/design/constraint-layout and XML fragment/activity layouts | Removed from active compilation; replaced by AndroidX ComponentActivity and one Compose screen/theme |
| JCenter, Kotlin Android Extensions/synthetic views, `kotlin-stdlib-jre7`, legacy test runner/Espresso and example arithmetic test | Removed; only Google Maven, Maven Central and Gradle Plugin Portal remain; real Compose instrumentation smoke tests replace template tests |
| Guava 25.1-android and Gson 2.8.0 | Removed as unused direct dependencies. AndroidX still brings its small transitive `com.google.guava:listenablefuture:1.0` API; no full legacy Guava library remains |
| Duplicate `com.frenchfrie` / `org.frenchfrie` Java UI, DAO, models and export code | Removed from active source; preserved in Git at original tip `26e83a75533d160fa636d61895cd48d76696b1d8` |
| Legacy launcher vectors | Retained as provisional template art, moved out of obsolete version-qualified folders; added monochrome layers and removed redundant PNG fallbacks now that minSdk is 26 |
| App backup/transfer | Disabled explicitly for pre-31 and 31+ platforms. Feature work must deliberately design backup/export for private choir material before enabling platform backup |

Useful historical references at that original commit include `app/src/main/java/com/frenchfrie/singasong/songs/Song.java`, `Couplet.java`, `SongContract.java`, and the `songs/export/` DTO/mapping/import-export examples. `app/src/androidTest/res/songs_export.json` is also retained in history. Retrieve a particular file with `git show 26e83a75533d160fa636d61895cd48d76696b1d8:PATH`. These are historical examples, not a new concert-pack contract or an import implementation. All 29 original commits were verified to remain ancestors of HEAD.

The maintainer-directed `venez-diner` reference was inspected read-only: its Kotlin DSL/catalog, `MainActivity.kt`, `docs/architecture/ui-architecture.md`, shared scaffold, list screen, central theme, agent guidance and mise configuration. Adopted patterns are the small Compose host, screen-owned insets/scaffolding, Material typography/card spacing, central light/dark theme and explicit dependency management. [Android architecture](../architecture/android.md) records exact reference paths and guidance for SAS-006/008/015. No reference credentials, branding, Koin, Room, DataStore, navigation graph or dependency versions were copied.

## SDK setup and license outcome

Inspected the complete license text presented by `sdkmanager` for the requested platform/build-tools/platform-tools/emulator/default-system-image packages before acceptance. The installer presented `android-sdk-license`, dated January 16, 2019; the reviewed text matched across the evaluated SDK tools. Under the maintainer's explicit setup authorization, accepted that specific requested license once and completed installation. The current [online SDK terms](https://developer.android.com/studio/terms) were also inspected; this is setup evidence, not a claim of legal review/approval for the product.

No blanket `sdkmanager --licenses` acceptance was used. The installed acceptance record is local to the SDK, not source control. Root setup/build/lint/test tasks do not pipe `yes`, and Gradle SDK auto-download is disabled. `setup:app` verifies installed files because the installer can exit successfully after a refusal. An initial tools 23.0 evaluation delegated to a separately downloaded Android CLI; the final 19.0 pin retains direct, versioned SDK package tooling with observable installation/license behavior. Other contributors must perform their own explicit setup.

## Actual validation

[Recorded command excerpts](evidence/sas-022/check-results.txt) preserve successful build/lint/device outputs; screenshots below come from the actual running APK. APK inspected with AAPT2: application ID `com.frenchfrie.singasong`, version code 1/name 0.1.0, min 26, target/compile 36, launcher `.MainActivity`. No Internet or dangerous runtime permission is requested; AndroidX adds its signature-protected dynamic-receiver permission.

| Check | Observed result |
| --- | --- |
| `mise tasks validate` | All six task definitions valid |
| `mise exec -- sh -c 'cd app && ./gradlew --no-daemon clean'`, then `mise run check:app` | Passed from cleared component build output: `assembleDebug lintDebug`, 46 tasks executed |
| `mise run build:app` | Passed; real debug APK at `app/build/outputs/apk/debug/SingASong-debug.apk` |
| `mise run lint:app` | Passed; `app/build/reports/lint-results-debug.txt`: `No issues found.` |
| `ANDROID_SERIAL=emulator-5556 mise run test:app` | API 36: two tests passed, zero failures/errors/skips; launcher display and activity recreation |
| `ANDROID_SERIAL=emulator-5558 mise run test:app` | API 26: same two tests passed, zero failures/errors/skips |
| Independent APK installs and `adb shell am start -W -n com.frenchfrie.singasong/.MainActivity` | Both APIs launched successfully; API 36 cold start reported 910 ms, API 26 total launch 455 ms. Single smoke observations, not performance benchmarks |
| Runtime dependency report and active-build search | Kotlin stdlib 2.2.10 and BOM-aligned Compose resolved; no old support libraries/plugins, full legacy Guava/Gson, JCenter or unresolved dependency entries |
| History ancestry | All 29 original commits retained; no destructive Git operation |
| `python3 scripts/check_docs.py` / `git diff --check` | Passed: 51 Markdown files checked; Git whitespace check clean |

API 36 target: KVM-accelerated Linux x86_64 emulator, Pixel 6 hardware profile, Android 16 image `BE2A.250530.026.D1/13818094`, 1080×2400 at 420 dpi, 2 GiB RAM and two virtual CPUs. API 26 target: Pixel 2 profile, Android 8.0 image `OSR1.180418.004/4931640`, 1080×1920 at 420 dpi. Both booted, installed, rendered and passed the real instrumentation suite. No SingASong entry was present in either device's crash buffer after validation.

API 36 display checks used actual Android settings/window-manager changes, UI hierarchy assertions and visual inspection:

- [Portrait](evidence/sas-022/api36-portrait.png), 1080×2400, and [landscape](evidence/sas-022/api36-landscape.png), 2400×1080: library heading and complete empty-state text visible after rotation; no overlap with system navigation.
- [Tablet-sized window](evidence/sas-022/api36-tablet-window.png), 1600×2560 at 240 dpi: centered content remains bounded to 640 dp. This is a resized emulator, not a claim of physical-tablet coverage.
- Narrow 600×1000 at 320 dpi (300×500 dp), font scale 1.5: lower body text remained reachable by vertical scrolling.
- [System dark mode](evidence/sas-022/api36-dark.png): theme and system-bar icon contrast changed; content remained visible.

Window overrides, rotation and font scale were restored after the display checks. Emulators were stopped after validation; reusable AVDs remain local validation infrastructure; SDK packages, APKs, generated reports, licenses and machine paths are not committed. The locally generated APK SHA-256 was `c69e4465652422954f5d105abd794d7cc866c9a9da205ec51cefefd0b7b7d1d0`; local debug signing means another machine's APK need not have that hash.

## Remaining limits and next work

SAS-022 does not implement choir workflows, PDF rendering, audio, persistence, safe import, backup/export, account access, network services or publication. SAS-006 needs the privately permission-cleared pilot corpus from SAS-003. Pilot physical phones/tablets, renderer/media compatibility and minimum-device policy still require feature-level validation. The palette and template launcher are provisional until SAS-015. SAS-002 must inspect Console/signing and choose release upload versions; debug success does not establish store readiness. SAS-007 owns CI/release automation. Actual execution was validated on Linux; the regenerated Windows wrapper was not executed on Windows. No non-routine setup blocker remains.
