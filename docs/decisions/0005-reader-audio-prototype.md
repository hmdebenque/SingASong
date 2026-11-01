# 0005: Local reader/audio prototype with generated fixtures

- Status: Accepted for the bounded prototype
- Date: 2026-10-31
- Categories: android, reader, playback, validation
- Agreement/evidence: Maintainer accepted proceeding with original generated fixtures while SAS-003 stays open, then explicitly authorized implementation. Component choices below are routine implementation choices within SAS-006, subject to later pilot evaluation.

## Decision

Implement one durable local practice workspace on the SAS-022 foundation. Use the platform `PdfRenderer` behind a `ScoreReader` interface, in an unexported isolated service receiving file descriptors. Bound rendered bitmaps and parser work. Use stable Media3 ExoPlayer for foreground playback with audio-focus and becoming-noisy handling. Pause when leaving the app; background sessions, A–B loops and speed controls remain SAS-009.

Use the Android document picker and copy size-bounded inputs to private storage under internal UUID names. Validate staged files before an atomic metadata commit, retain the previous import on failure, and remove orphaned imports on reopening. A versioned local JSON document is sufficient for one score/recording plus page/audio position. This does not choose persistence for the full SAS-008 library or a concert-pack schema.

Generated assets exercise the same ingestion path as external files. They cover vector/scan-like/extreme PDFs, WAV/MP3 and invalid inputs, with a deterministic generator and hash manifest. Clearly identify demo material. Keep PDF pages and recording times independent: a rendered PDF provides no playable notes or synchronization.

## Rationale and limits

`PdfRenderer` is available below the provisional API 26 minimum and adds no bundled native renderer/dependency license. [Platform guidance](https://developer.android.com/reference/android/graphics/pdf/PdfRenderer) recommends isolated rendering for untrusted documents. AndroidX PDF remained a beta during evaluation; this prototype does not require its newer editing/search surface. Media3 gives one playback engine for the later recording feature; pin/version evidence is in the [implementation report](../project/sas-006-prototype.md).

The `venez-diner` reference's separation of screen state/ViewModel and repository logic inspired the boundary; no Room, Koin or unrelated behavior was copied. Current [Android architecture](../architecture/android.md) describes actual ownership.

SAS-003 retains real choir corpus, content permissions, workflow and physical-device validation. Synthetic acceptance does not complete Phase 0 or establish production PDF/media safety, visual identity, musical usefulness or Play readiness.
