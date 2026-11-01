# Android implementation

The app is a Kotlin AndroidX Compose/Material 3 application under `app/`. SAS-022 supplied the modern toolchain; SAS-006 supplied the reader/player; SAS-008 adds a local multi-song library. See the [component README](../../app/README.md), [modernization evidence](../project/sas-022-modernization.md) and [reader/audio evidence](../project/sas-006-prototype.md). [Library evidence](../project/sas-008-library-evidence.md) records the first slice. Lyrics, preferred-part/filtering, backup/restore, programmes and accounts remain planned.

## Current ownership

- `MainActivity.kt` hosts Compose and a retained `PracticeViewModel`, enables edge-to-edge drawing, and pauses playback on actual backgrounding. Configuration changes keep the ViewModel/player alive.
- `ui/screens/LibraryScreen.kt` owns its scaffold, safe drawing/IME insets, searchable song list, scrollable metadata/material dialogs and document-picker launchers. The picker remembers its originating arrangement/label/part across recreation; cancel performs no import. Library/back navigation checkpoints and pauses. A phone stacks the scrollable PDF/page controls and recording controls; windows at least 720 dp wide use two panes. Zoom toggles fit-width/2× with pan/scroll. It renders observable state and sends actions to the ViewModel.
- `ui/theme/Theme.kt` owns provisional light/dark Material schemes. Strings and Material typography remain centralized; SAS-015 owns reviewed identity.
- `practice/PracticeViewModel.kt` coordinates import, render and playback jobs, exposes immutable `StateFlow`, and retains independent page/audio state. Position checkpoints carry their originating resource IDs and are written while playing and on seek/page/zoom/pause actions. Switching checkpoints before selection, stops obsolete audio and cancels/identity-checks obsolete rendering. Cold start prepares playback paused.
- `library/LibraryStore.kt` owns schema-version-1 platform SQLite metadata, foreign keys, relationship indexes, transactions and private resource files. Songs → arrangements → resources are distinct from resource state and selections. Unicode-decomposed, combining-mark-stripped, locale-independent lowercase substring search includes title/composer/tags/arranger while retaining display spelling. Imports stage/sync/validate/move before committing and never replace another resource. IO and a repository mutex serialize writes; no Room/compiler dependency or toolchain change was needed. Unsupported upgrades/downgrades fail without resetting data.
- `practice/WorkspaceStore.kt` remains the SAS-006 format reader and test fixture. Conversion reads without its old cleanup, validates private copies, then transactionally commits all relationships/positions and a conversion marker. Failure retains originals and existing library content, reports retry, and cannot mark conversion complete. Originals and crash-orphan files are retained; no automatic deletion/sweep is implemented. Source URIs are never retained.
- `reader/ScoreReader` separates rendering from workspace/UI. `PdfReader` binds to an unexported isolated `PdfWorkerService` via a small AIDL interface. Only explicitly passed input/output descriptors cross the boundary. Work runs off the UI thread; page bitmaps are capped at 2048 pixels per side and returned through a temporary PNG file, avoiding large Binder bitmap transactions. Worker calls have a process watchdog.
- `practice/PracticePlayer.kt` wraps stable Media3 ExoPlayer. It prepares staged files for validation, opens private files, handles audio focus/noisy events, and releases player resources with the ViewModel. There is no MediaSession/background service in this prototype.
- `src/androidTest/` exercises imports/rollback, parser/decoder fixtures, resource bounds, state restoration, real playback/seek, recreation, backgrounding and focus loss. The fixture [generator and provenance](../../app/scripts/README.md) are repository-owned synthetic material.
- `gradle/libs.versions.toml` pins direct dependencies and BOM-managed Compose. AGP supplies built-in Kotlin and the Compose compiler plugin matches it. The SAS-022 matrix remains intact; feature dependencies align with its SDK/Kotlin requirements.

Backend and concert-pack contract ownership remains in [repository architecture](repository.md). There is a local SQLite database; no dependency-injection framework, navigation library, network client or backend integration is introduced. PDFs provide images of pages, not notes or an audio timeline. Saved files are private and platform backup remains disabled pending deliberate backup/export design.

## VenezDiner reference

The maintainer identified `venez-diner` as architecture/design inspiration on 2026-10-30. It was inspected read-only. Paths below are relative to that reference repository; its checkout is not a build dependency.

| Reference path | Adaptation |
| --- | --- |
| `app/build.gradle.kts`, `gradle/libs.versions.toml` | Kotlin DSL, centralized dependency versions and a Compose BOM; SingASong selects its own compatible pins. |
| `app/src/main/java/org/frenchfrie/venezdiner/MainActivity.kt` | Small Compose host; SingASong retains a small host; list/song navigation is ViewModel state without a navigation dependency. |
| `docs/architecture/ui-architecture.md`, `ui/components/VenezDinerScaffold.kt` under the same Java package | Screen-owned scaffolding and Compose inset handling. SingASong uses safe drawing insets and SAS-008 adds IME-aware, scrollable input dialogs/list content. |
| `ui/screens/DinnersListScreen.kt`, `ui/theme/Theme.kt` under the same package | Material spacing, truthful empty state and central light/dark themes, adapted to choir material without dinner branding/workflows. |
| `ui/viewmodel/DinnersViewModel.kt`, repository ownership described in its `AGENTS.md` | SAS-006 adapts immutable observable state and separates screen orchestration from private storage/render/player operations. SAS-008 keeps these boundaries around platform SQLite without Koin/Room. |
| `AGENTS.md`, `mise.toml` | Explicit ownership and tool entry points; this repository's workflow and tool pins remain authoritative. |

SAS-008 now separates library relationships and persistence from reader/player state; future schema complexity may justify Room. SAS-015 can use the reference's component/theme organization while developing a choir-specific identity. Reference versions, branding and persistence/injection choices are not inherited.
