# SAS-003: Real choir materials and pilot validation

Status and assignment: [backlog](backlog.md). Capability: Standard. Acceptance requires maintainer/choir feedback and real content; generated fixtures alone cannot close this task.

## Starting evidence — 2026-10-31

SAS-022 and SAS-006 are complete under their recorded technical scope. The current debug APK exists at `app/build/outputs/apk/debug/SingASong-debug.apk`. No device/emulator was attached when `mise exec -- adb devices -l` was checked on resumption. Only generated PDF/audio fixtures were found in the project; real pilot material remains unprovided. Prior emulator results are in the [prototype report](sas-006-prototype.md).

The maintainer has been asked for the private material location/permission, pilot device and voice part, primary use case and whether manual concert-pack sharing is acceptable. The maintainer selected a roughly four-year-old flagship Pixel; this is implemented as the Pixel 7 Pro emulator profile with Android 16/API 36. See the [setup guide](../guides/pixel-emulator.md). Private material, voice part, primary use and sharing preferences remain pending; none are inferred from the technical prototype.

## Material intake

Begin with a small representative set, then extend toward the roadmap's 10–20 songs if available. Useful cases are a digital PDF, a scanned PDF, a dense/multipage score, two arrangements of one song, and a tutti/individual-part recording pair. Record missing cases explicitly rather than blocking every check until the corpus is complete.

Keep actual scores, recordings, private filenames, storage paths and identifiable feedback outside Git. In committed results use anonymous material IDs, technical properties and permission status only. Do not upload material to third-party services. Permission for private evaluation does not establish permission for redistribution, screenshots, demo bundling or store artwork.

For each material, privately record:

| Field | Purpose |
| --- | --- |
| Anonymous ID and arrangement grouping | Distinguish files and versions without publishing repertoire |
| PDF type, page count, bytes and page dimensions | Assess scans, density and import/render limits |
| Audio container/codec, duration, bytes and part label | Assess actual decoding and practice usefulness |
| Permission/provenance and permitted use | Establish private-test scope separately from distribution |
| Source device/location | Reproduce import privately; never commit credentials or share links |

Current caps are 20 MB/300 pages for PDFs and 64 MB for audio, with rendered pages capped at 2048 pixels per side. Record a rejected real file as a finding; do not silently resample material or increase limits to claim success. Password-protected PDFs currently lack a password-entry flow.

## Pilot sequence

Use copies of original files. The prototype holds one practice workspace; importing a replacement replaces that workspace's resource. It has no full library or backup/export yet.

1. Record phone/tablet model, Android version, screen size, chosen voice and reading distance. Install the debug APK on an authorized test device.
2. Import a real PDF through the system picker. Inspect a dense page, scan and final page against the original. Record legibility at normal reading distance and when zooming.
3. Turn pages while holding the device or placing it on a stand. Check portrait/landscape, large text and whether controls obscure notation. Distinguish renderer defects from suggestions for future reader controls.
4. Import a permitted recording. Read while listening, pause and seek to a passage; check audible output on the intended speaker/headphones. PDF page and audio time are deliberately independent.
5. Relaunch and confirm files, page and last saved audio position restore with playback paused. Verify offline use after import. Test source independence using a disposable copy, never by deleting original choir files.
6. Try a canceled or rejected import and verify the previous workspace remains usable. Do not clear app data or uninstall before preserving material elsewhere.
7. Record an actual interruption/backgrounding result. Current playback pauses when leaving the app; background playback, speed control and A–B loops belong to SAS-009.
8. Gather organiser/singer feedback: current file distribution, arrangement/part naming, programme preparation, offline needs and whether manual pack updates are practical.

## Results record

For each session record date, APK version/hash, anonymous material ID, device/API, scenario, observed result (Pass / Fail / Not tested), severity and follow-up task. Keep identifying details in private notes. No physical-device or real-score result is recorded yet.

| Decision informed | Required evidence | Current state |
| --- | --- | --- |
| Minimum SDK and target devices | Pilot device inventory | Pixel 7 Pro emulator selected; physical devices/tablet inventory and minimum-SDK confirmation pending |
| PDF engine/resolution suitability | Actual dense/scan score readability | Pending |
| Audio support and interruption needs | Real recordings and intended output devices | Pending |
| Library metadata and navigation (SAS-008) | Actual arrangement/part conventions and search needs | Pending |
| Hosted MVP versus packs (SAS-004) | Organiser distribution/update workflow | Pending |
| Data/rights inventory (SAS-005) | Material provenance and intended sharing audience | Pending |

## Completion gate

Representative permitted material is available privately; device/workflow requirements are recorded; a real score and recording have been exercised offline on a pilot device; findings and limitations are linked to follow-up work. The maintainer validates the resulting workflow requirements. Missing cases remain explicit. This does not claim a completed choir library, distribution system or Play release.

If material remains unavailable, keep SAS-003 open and choose a bounded implementation task that can use generated fixtures. Do not silently remove this pilot gate or mark musical usefulness as verified.

## Reference emulator check — 2026-10-31

Pixel 7 Pro profile `singasong-pixel7pro-api36` was created using the installed default Android 16/API 36 x86_64 image. Boot, APK installation and launch succeeded at 1440×3120 / 560 dpi. All nine existing generated-fixture tests passed (zero failures/errors/skips); see [test output](evidence/sas-003/pixel7pro-tests.txt) and the [reusable setup guide](../guides/pixel-emulator.md). The app was reinstalled/launched after test cleanup and the emulator stopped. No physical-device or real choir material result is claimed; the pilot completion gate remains open.
