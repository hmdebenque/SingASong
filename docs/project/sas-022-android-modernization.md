# SAS-022: Modern Android foundation

Status and assignment: see the [backlog](backlog.md).

- Milestone: M0; priority: P0; depends on SAS-001.
- Capability: Standard.
- Suggested agent: Sol or a suitable Claude implementation model; actual assignment is recorded in the backlog.
- Review needs: compatible version matrix, removal of obsolete dependencies, reproducible build and device/emulator launch.
- Escalation: library or renderer constraints require a significant architectural choice.

## Outcome

A minimal native Kotlin app with a stable, compatible 2026 framework/toolchain, under `app/`, built through root mise tasks. The maintainer confirmed the old app was never published and has no users, so legacy user-data migration is not required. Preserve Git history; legacy screens and dependencies need not constrain the reboot.

## Scope

1. Inventory the existing build and dependencies. Current files use Kotlin 1.2.30, AGP 3.1.2, Gradle 4.4, SDK 27, Android support libraries, JCenter and Kotlin Android Extensions. Record retain/replace/remove decisions; do not blindly update unused libraries.
2. Select and pin mutually compatible stable Android Gradle Plugin, Gradle wrapper, JDK, Kotlin/Compose compiler configuration and Android SDK versions. Check AGP's Kotlin integration and compatibility requirements; do not combine independently selected latest versions. Use official release documentation at implementation time and record the verified version matrix.
3. Use AndroidX and a minimal Jetpack Compose/Material 3 screen as the proposed implementation baseline. Use Kotlin Gradle DSL and a version catalog for explicit dependency management; use the stable Compose BOM for Compose library alignment. Avoid preview dependencies without a concrete documented need.
4. Remove JCenter, obsolete support dependencies, Kotlin Android Extensions and unused legacy build configuration. Keep useful domain/import examples in history or an explicitly documented reference location; do not leave obsolete code in active compilation solely to preserve it.
5. Update mise JDK/SDK setup and build tasks together. Keep all Android build files inside `app/`. SDK license review/acceptance remains a setup step; do not automatically accept legal terms as a side effect of validation. For this implementation session the maintainer explicitly authorized necessary local SDK installation and acceptance after inspecting terms, superseding generic confirmation guidance. Record the actual outcome.
6. Set compile/target SDK for current stable tooling and Play requirements. Select minimum SDK deliberately based on intended devices and dependency requirements, documenting the provisional choice until SAS-003 supplies the pilot inventory. Keep `com.frenchfrie.singasong` as the provisional application ID pending SAS-002; Console access is not required for local debug builds.
7. Expose meaningful build/lint/test tasks and update component/development documentation. Validate a minimal screen on a device or emulator, including basic resizing/rotation. Report unavailable verification explicitly.

Do not add Room, Media3, networking, authentication or a PDF engine merely to fill out the dependency list. Add them in the feature task that uses them. SAS-006 owns the PDF/audio prototype; this task does not implement choir product workflows or publish an app.

## Implementation record

The maintainer-directed `venez-diner` reference was inspected read-only for architecture, UI and tooling patterns; see [Android architecture](../architecture/android.md). The [SAS-022 implementation report](sas-022-modernization.md) records the resulting pins, source disposition and actual acceptance evidence. Task status remains authoritative in the backlog.

## Acceptance evidence

- Exact compatible versions and official source references recorded; no dynamic dependency versions.
- Root mise tasks produce a debug APK from the modern build; lint and relevant actual checks pass.
- Minimal app launches on an emulator/device; report device/API and rotation/resizing result.
- Dependency inspection confirms obsolete plugins/repositories/support libraries are absent from the active build.
- History remains intact; app identity and source disposition are documented; no claim of legacy data migration.
- Documentation checks and Git whitespace checks pass; no SDK/signing secrets or machine-specific paths committed.

## Official starting references

- [Android Gradle Plugin compatibility and releases](https://developer.android.com/build/releases/about-agp).
- [Compose BOM](https://developer.android.com/develop/ui/compose/bom): aligns Compose libraries; compiler/toolchain compatibility must also be checked.
- [Play target SDK requirements](https://developer.android.com/google/play/requirements/target-sdk): checked 2026-10-30; phone/tablet submissions currently require API 36 or higher. Recheck before publication.

These references inform implementation; their sample versions are not evidence of a validated project build.
