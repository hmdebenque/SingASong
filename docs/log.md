# Knowledge change log

## 2026-11-01 — Save library checkpoint

Committed SAS-008 first library slice, test evidence, repository ownership and resume instructions as `399c819`. Created a verified local Git bundle and saved debug APK; no push or publication. SAS-008 parent remains In progress for lyrics, preferred-part behavior, backup/restore and the real choir pilot.

## 2026-10-31 — SAS-008 first library slice delivered

Sol accepted full ownership and implemented SQLite-backed songs/arrangements/resources, editable song metadata, normalized search, labelled PDF/recording selection and durable resource-specific positions around the existing isolated reader/foreground player. SAS-006 conversion copies/validates files before a transactional metadata/marker commit, retains originals and supports safe retry. Imports append independent private resources; canceled/failed attempts keep committed content. No dependency/toolchain change.

Build/lint/fixture checks and 19 instrumentation tests on each Pixel 7 Pro/API 36 and API 26 passed. Actual picker/cancellation, source removal plus airplane-mode force-stop/reopen, missing-file recovery, rotation, 200% text/IME and larger-window navigation were checked. See [implementation/evidence and exact remaining scope](project/sas-008-library-evidence.md). SAS-008 stays In progress for lyrics, preferred-part/filtering, backup/restore and real Phase 1 pilot acceptance; SAS-003 stays open.

The current owned repository is [hmdebenque/SingASong](https://github.com/hmdebenque/SingASong); the maintainer authorized reboot restructuring/reset when useful, superseding historical preservation/pull-only constraints. [Ownership evidence](project/repository-ownership.md) records the origin and limits. No reset/push/remote tracker update or publishing occurred; active uncommitted pilot/marketing work was preserved.

## 2026-10-31 — Offline library implementation authorized

Maintainer approved proceeding with SAS-008 using generated fixtures and Sol implementation. Defined a bounded library slice, multi-resource state integrity and existing-prototype preservation requirements; broader lyrics/preferred-part/backup acceptance remains tracked before parent completion. Prepared a fresh Orca Sol session with uninterrupted local execution permissions.

## 2026-10-31 — Pixel reference emulator

Selected Pixel 7 Pro (2022) for the maintainer-requested four-year-old flagship target. Created a dedicated API 36 AVD using an already installed image; boot/install/launch and all nine generated-fixture tests passed. Added reproducible commands and retained test evidence. Real choir validation remains open.

## 2026-10-31 — Resume with real choir pilot preparation

Started SAS-003 intake and prepared private-corpus handling, real-device scenarios and an explicit pending-results record. Confirmed the saved APK exists and no device is attached. Real material and workflow inputs remain pending; no pilot success or additional feature implementation is claimed. Existing marketing/audio edits were left untouched.

## 2026-10-31 — SAS-006 offline technical prototype completed

The maintainer authorized original generated score/audio fixtures while real choir material is unavailable; removed SAS-003 as a prerequisite of technical SAS-006 implementation and retained it as the real choir/pilot validation gate. Implemented one private workspace with system-picker imports, staged size/format checks, atomic metadata, isolated platform PDF rendering, page/zoom controls, foreground Media3 playback/seek and independent restored positions. Original fixture provenance/generation and bounded prototype decisions are documented.

Build/lint and fixture checks passed. Nine device tests passed on each API 26/36 emulator, including import rollback, actual parser/decoder/player behavior, saved state, backgrounding and focus loss. Real document-picker import/cancellation/corrupt-file feedback, source deletion plus airplane-mode restart, phone/tablet-sized layouts, zoom, dark mode and short-window large text were exercised. Documentation and Git whitespace checks passed. Marked the technical SAS-006 scope Done with [evidence](project/sas-006-prototype.md); no real choir corpus, physical-device, audible-headset or Phase 0 completion claim is made. No app publication or deployment occurred.

## 2026-10-31 — SAS-022 modern Android foundation completed

Replaced the active legacy framework/build with a minimal Kotlin AndroidX Compose/Material 3 application under `app/`, retaining the original history and application ID. Pinned compatible AGP/Gradle/Kotlin/Compose/JDK/SDK configuration together, removed obsolete repositories/plugins and unused legacy dependencies, and added real root mise build/lint/device checks. Necessary SDK terms were inspected and the requested license accepted under the maintainer's explicit setup authorization. Clean build/lint and both launch/recreation tests passed on API 26 and API 36 emulators; API 36 rotation, resizing, large text and dark mode were checked. Documentation and Git whitespace checks passed. Marked SAS-022 Done with [implementation evidence](project/sas-022-modernization.md).

The maintainer-directed `venez-diner` reference was inspected read-only. Adapted small Compose hosting, screen-owned layouts/insets, centralized themes and explicit dependency conventions; exact paths and future feature/design guidance are in [Android architecture](architecture/android.md). No reference branding, secrets or unrelated stack choices were adopted. Palette/template launcher and minSdk 26 remain provisional. PDF/audio stays in SAS-006; no app publication or deployment occurred.

## 2026-10-30 — Unpublished app and modern Android foundation

Maintainer confirmed no publication or users and requested framework/library modernization for 2026. Removed legacy installed-user migration from current planning, narrowed SAS-002 to unpublished Console configuration, and added Ready task SAS-022 for a modern foundation independently of pilot material/Console access. SAS-006 now depends on SAS-022 and pilot content. Historical SAS-001 evidence is unchanged; no build/dependency changes are claimed in this planning update.

## 2026-10-30 — SAS-001 closed with local tracking

The maintainer authorized closure and removed GitHub tracker management from required scope. Marked SAS-001 Done with linked history/source and planning-reconciliation evidence. The local backlog remains the single status/assignment authority; archived the prepared GitHub import proposal and left legacy issues untouched. The Android layout and mise environment follow explicit maintainer direction. SDK license acceptance and APK/device verification remain SAS-006 follow-up work. No remote code push, default-branch merge, publication or deployment occurred.

## 2026-10-30 — Mise Android environment provisioning

Following maintainer direction, pinned Temurin Java 8 for Gradle, Android SDK command-line tools 13.0, and a task-specific Temurin Java 17 for SDK provisioning. Added `setup:app` as a dependency of `build:app`, with idempotent SDK platform 27 / Build Tools 27.0.3 installation and post-install verification. SDK license acceptance remains interactive. Installed all pinned tools and verified Gradle 4.4 starts on the managed Java 8; SDK setup reached its license prompt. This does not complete SAS-006/007.

## 2026-10-30 — Android Gradle ownership and mise entry point

Following explicit maintainer direction, moved the wrapper and Gradle configuration under `app/`, combined the root buildscript with the existing application module configuration, and made the Android application a single Gradle project without moving source/resources. Added root mise tasks `build:app` and `check:docs`; recorded legacy build prerequisites. Mise task validation and dry run passed; the actual task downloaded Gradle 4.4 and failed because it cannot recognize Java 21, before project evaluation. No APK was generated. This is a build-layout change, not the SAS-006 modern Android foundation or completion of SAS-007 delivery tooling.

## 2026-10-30 — Android directory confirmed

The maintainer confirmed `app/` as the Android application directory for existing and future work. Moved the Android scope README there and updated ownership guidance and links. Existing Android source and Gradle build files remain unchanged. Broader repository conventions and stack choices remain Proposed.

## 2026-10-30 — SAS-001 local integration; tracker access blocked

Backed up the latest 25 planning files, imported the original complete 29-commit Git history and retained all legacy source/build files in their original locations on `sas-001-planning-tracker`. Reconciled the title-only legacy README with the planning README. No new stack was selected or legacy application migrated. Prepared exact SAS tracker material; GitHub pull-only permissions and HTTP 404 on label creation prevent configuration. The two legacy issues remain untouched and excluded. SAS-001 is Blocked, not Done; the local backlog stays authoritative. See the [integration report](project/sas-001-integration.md) and [tracker package](project/tracker/README.md).

## 2026-10-30 — Provider-neutral task assignment

Following maintainer agreement, added Routine/Standard/Deep reasoning capability levels, review needs, escalation triggers and separate actual model/session assignment fields to the workflow and issue template. Classified all 21 seed tasks and recorded the accepted SAS-001 handoff to Sol without claiming verified execution. Claude and human contributors use the same workflow.

## 2026-10-30 — Multitechnology project management

Adapted dsr-engine's documentation index, decision lifecycle, component ownership and contributor templates. Added Android, website, backend and infrastructure scope directories, delivery milestones, dependency-aware seed backlog, release evidence template and documentation checks. Preserved ROADMAP.md as the canonical detailed plan. Backend/website stacks and hosted-MVP scope remain proposals. No remote issues, repository initialization, applications or deployments were created.
