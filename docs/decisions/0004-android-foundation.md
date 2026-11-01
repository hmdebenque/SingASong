# 0004: Modern Android foundation

- Status: Accepted
- Date: 2026-10-30
- Categories: android, tooling
- Agreement/evidence: Maintainer's explicit SAS-022 implementation authorization for a minimal Kotlin AndroidX Compose/Material 3 foundation, Kotlin DSL, version catalog, stable compatible tooling, and local build/device validation.

## Decision

Replace the unpublished legacy framework in the active source set. Keep Android build ownership under `app/`, and retain `com.frenchfrie.singasong` pending the SAS-002 Console audit. No legacy production user-data migration is required because there are no users. The maintainer subsequently authorized restructuring the owned reboot repository; history remains available as reference but is no longer a design constraint. See [repository ownership](../project/repository-ownership.md). Current SAS-006 prototype files and positions are preserved by SAS-008 conversion.

Use AGP's built-in Kotlin, a matching Compose compiler plugin, a stable Compose library BOM and one mise-managed JDK for Gradle and SDK tools. Exact implementation pins and official compatibility sources live in the [SAS-022 report](../project/sas-022-modernization.md). Minimum API 26 is provisional until SAS-003 inventories pilot devices. Compile/target API 36 meets the currently documented phone/tablet submission floor; publication still requires a fresh policy/Console audit.

Adopt a small Compose activity, screen-owned scaffolding and central light/dark Material themes. The maintainer-directed `venez-diner` reference was inspected read-only; adopted patterns and reference paths are recorded in [Android architecture](../architecture/android.md). That reference does not select dependencies, persistence, injection, navigation or branding for SingASong.

## Consequences and limits

The app is a launchable empty foundation, not a reader or playback prototype. SAS-006 adds real PDF/audio. The provisional palette and historical template launcher art are not SAS-015 brand approval. Version code 1 and version name 0.1.0 are local bootstrap values, not verified Play upload values. No publishing, release signing or hosted service is configured.
