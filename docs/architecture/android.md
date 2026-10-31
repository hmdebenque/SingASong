# Android implementation

The app is a Kotlin AndroidX Compose/Material 3 application under `app/`. SAS-022 supplied the modern toolchain; SAS-006 adds one local score/recording workspace. See the [component README](../../app/README.md), [modernization evidence](../project/sas-022-modernization.md) and [reader/audio evidence](../project/sas-006-prototype.md). Full library/programme/account features remain planned.

## Current ownership

- `MainActivity.kt` hosts Compose and a retained `PracticeViewModel`, enables edge-to-edge drawing, and pauses playback on actual backgrounding. Configuration changes keep the ViewModel/player alive.
- `ui/screens/LibraryScreen.kt` owns its scaffold, safe drawing insets and document-picker launchers. A phone stacks the scrollable PDF/page controls and recording controls; windows at least 720 dp wide use two panes. Zoom toggles fit-width/2× with pan/scroll. It renders observable state and sends actions to the ViewModel.
- `ui/theme/Theme.kt` owns provisional light/dark Material schemes. Strings and Material typography remain centralized; SAS-015 owns reviewed identity.
- `practice/PracticeViewModel.kt` coordinates import, render and playback jobs, exposes immutable `StateFlow`, and retains independent page/audio state. Position checkpoints are written while playing and on seek/page/pause actions. Cold start prepares playback paused.
- `practice/WorkspaceStore.kt` owns a single versioned private workspace. Size-bounded imports use UUID filenames, staging, validation, private copies and an atomic JSON commit. File-picker URIs are not retained. Failed imports retain the prior committed file; reopening cleans unreferenced staging/orphan files. This does not select Room or a full library schema for SAS-008.
- `reader/ScoreReader` separates rendering from workspace/UI. `PdfReader` binds to an unexported isolated `PdfWorkerService` via a small AIDL interface. Only explicitly passed input/output descriptors cross the boundary. Work runs off the UI thread; page bitmaps are capped at 2048 pixels per side and returned through a temporary PNG file, avoiding large Binder bitmap transactions. Worker calls have a process watchdog.
- `practice/PracticePlayer.kt` wraps stable Media3 ExoPlayer. It prepares staged files for validation, opens private files, handles audio focus/noisy events, and releases player resources with the ViewModel. There is no MediaSession/background service in this prototype.
- `src/androidTest/` exercises imports/rollback, parser/decoder fixtures, resource bounds, state restoration, real playback/seek, recreation, backgrounding and focus loss. The fixture [generator and provenance](../../app/scripts/README.md) are repository-owned synthetic material.
- `gradle/libs.versions.toml` pins direct dependencies and BOM-managed Compose. AGP supplies built-in Kotlin and the Compose compiler plugin matches it. The SAS-022 matrix remains intact; feature dependencies align with its SDK/Kotlin requirements.

Backend and concert-pack contract ownership remains in [repository architecture](repository.md). There is no database, dependency-injection framework, navigation graph, network client or backend integration in this slice. PDFs provide images of pages, not notes or an audio timeline. Saved files are private and platform backup remains disabled pending deliberate backup/export design.

## VenezDiner reference

The maintainer identified `venez-diner` as architecture/design inspiration on 2026-10-30. It was inspected read-only. Paths below are relative to that reference repository; its checkout is not a build dependency.

| Reference path | Adaptation |
| --- | --- |
| `app/build.gradle.kts`, `gradle/libs.versions.toml` | Kotlin DSL, centralized dependency versions and a Compose BOM; SingASong selects its own compatible pins. |
| `app/src/main/java/org/frenchfrie/venezdiner/MainActivity.kt` | Small Compose host; SingASong retains one workspace and does not introduce a navigation graph prematurely. |
| `docs/architecture/ui-architecture.md`, `ui/components/VenezDinerScaffold.kt` under the same Java package | Screen-owned scaffolding and Compose inset handling. SingASong uses safe drawing insets and will add IME rules with actual input features. |
| `ui/screens/DinnersListScreen.kt`, `ui/theme/Theme.kt` under the same package | Material spacing, truthful empty state and central light/dark themes, adapted to choir material without dinner branding/workflows. |
| `ui/viewmodel/DinnersViewModel.kt`, repository ownership described in its `AGENTS.md` | SAS-006 adapts immutable observable state and separates screen orchestration from private storage/render/player operations. No Koin or Room is needed for this workspace. |
| `AGENTS.md`, `mise.toml` | Explicit ownership and tool entry points; this repository's workflow and tool pins remain authoritative. |

SAS-008 should revisit data/domain boundaries when a real multi-song library appears. SAS-015 can use the reference's component/theme organization while developing a choir-specific identity. Reference versions, branding and persistence/injection choices are not inherited.
