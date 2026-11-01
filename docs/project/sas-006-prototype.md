# SAS-006 offline reader/audio prototype

Date: 2026-10-31. Owner: OpenAI GPT-6.1 Sol, high reasoning, continuing the existing checkout/session. Task state is authoritative in the [backlog](backlog.md); observable acceptance is defined in the [task specification](sas-006-reader-audio.md).

## Scope and delivered behavior

The maintainer authorized implementation with original generated fixtures because choir assets are unavailable. SAS-003 remains open for real choir content, content permissions, workflow feedback and pilot-device inventory. Its former prerequisite on SAS-006 was changed explicitly; technical completion does not complete the real choir pilot or Phase 0.

The app now offers one local practice workspace:

- Open/replace a PDF and recording with Android's system document picker. Cancellation leaves the workspace unchanged.
- Copy files into app-private storage under internal UUID names; validate before replacing the previously committed file. A PDF is capped at 20 MB/300 pages and recording at 64 MB. Empty, corrupt, interrupted and oversized imports preserve the previous file.
- Render actual PDF pages, navigate independently of playback, and switch between fit-width and 2× zoom with pan/scroll. Landscape/tablet windows at least 720 dp wide use two panes. Short narrow windows let the complete workspace scroll so controls stay reachable.
- Play/pause and seek an actual recording while reading. Show elapsed time/duration, handle audio focus and pause on backgrounding. Cold start prepares the recording paused. PDF content does not supply notes or musical synchronization.
- Restore private files, selected page and recording position after activity recreation or process restart. Checkpoint position approximately every 500 ms during progress and on navigation/seek/pause; an abrupt stop can lose the latest uncommitted fraction of a second.
- An explicitly labelled **Try generated demo** action imports original synthetic PDF/WAV material through the same storage/validation path. The app does not silently substitute demo content for the user's files.

No library catalogue, arrangements editor, pack schema, network service, accounts, notation playback, annotations, speed adjustment, A–B repeat or background media session is claimed. Those remain their feature tasks.

## Renderer and playback selection

| Choice | Evidence / rationale | Limits |
| --- | --- | --- |
| Platform `PdfRenderer` behind `ScoreReader` | Available from API 21; works with the provisional API 26 minimum. [Platform documentation](https://developer.android.com/reference/android/graphics/pdf/PdfRenderer) requires a seekable descriptor and recommends isolated rendering for untrusted input. | Platform behavior varies by OS/PDF engine; no OCR, search, annotations or password flow in this prototype. |
| Unexported isolated `PdfWorkerService` / AIDL | Caller passes only staged input and temporary output descriptors. Parsing and rendering run outside the main app process; bitmaps are capped at 2048 pixels per side, at most 16 MiB ARGB each. PNG output uses a file descriptor instead of a large Binder bitmap. Worker calls have a 20-second process watchdog; binding has a 10-second timeout. | PDF engine internals may allocate additional native memory. This is a bounded prototype, not a complete hostile-document security audit. A provider that stalls while copying may require leaving/restarting the operation; source-copy timeout enforcement is not implemented. |
| Platform renderer instead of AndroidX PDF | [AndroidX PDF release notes](https://developer.android.com/jetpack/androidx/releases/pdf) still listed a beta during evaluation. No additional PDF library/native engine is bundled. | Synthetic scan-like/vector/extreme-dimension cases cannot establish fidelity for the future choir corpus. |
| Media3 ExoPlayer 1.9.4 | Stable maintenance release verified in [official release notes](https://developer.android.com/jetpack/androidx/releases/media3#1.9.4) and [published artifact metadata](https://dl.google.com/dl/android/maven2/androidx/media3/media3-exoplayer/1.9.4/media3-exoplayer-1.9.4.pom). The 1.9 family minimum API 23 fits API 26; Kotlin dependencies resolve with the SAS-022 compiler. | Only ExoPlayer is added, with no network/session/UI extension stack. [Supported formats](https://developer.android.com/media/media3/exoplayer/supported-formats) depend on container, encoding and platform support; actual fixtures cover PCM WAV and MP3. |
| Delegated focus/noisy handling, foreground lifecycle | [Audio-focus guidance](https://developer.android.com/media/optimize/audio-focus) recommends ExoPlayer's managed focus; tests exercise focus loss and backgrounding. ViewModel cleanup releases the player. | Physical headset/call interruption behavior and audible output on pilot hardware remain unverified. Background playback belongs to SAS-009. |

Other direct feature pins are lifecycle ViewModel/runtime-compose 2.9.4, coroutines-android 1.10.2 and core-ktx 1.16.0, aligned with the already resolved AndroidX foundation. AGP 9.1.1, Gradle 9.3.1, JDK 17, Kotlin/Compose compiler 2.2.10, Compose BOM 2026.02.01 and SDK 36/minimum 26 remain unchanged. Build/AAR metadata, lint and actual device runs verify the combined matrix.

The [bounded prototype decision](../decisions/0005-reader-audio-prototype.md) and [Android architecture](../architecture/android.md) record ownership. The maintainer-directed VenezDiner reference's ViewModel/observable-state/repository separation informed the implementation. No Room, Koin, DataStore or navigation framework was copied or selected for the full library.

## Private storage and failure behavior

A versioned local JSON document stores the single workspace and independent positions. It uses Android `AtomicFile`; it is not a backend API or concert-pack contract. Imported content stays in private `files/practice/`. A staged copy is size-checked and flushed, then the isolated PDF worker opens/decodes its first page or ExoPlayer prepares the recording and obtains a positive duration. The copy is renamed and metadata committed before the previous resource is deleted. Metadata updates use resource IDs so a delayed position checkpoint cannot overwrite a replacement's state.

Validation checks the first PDF page or initial recording readiness, not complete byte-by-byte fidelity; later page/decoder failures are still possible and reported. Restart removes abandoned/unreferenced imports after reading the committed metadata successfully. A failed restore preserves stored files and reports an error. Rendering output is a temporary cache file deleted after decoding. No source URI is retained and no broad storage permission is requested. Platform backup/transfer remains disabled. Media3 transitively contributes `ACCESS_NETWORK_STATE` and `WAKE_LOCK`; no wake mode or Internet permission is enabled. There is no upload or account integration.

## Synthetic material and reproducibility

The [fixture guide](../../app/scripts/README.md) records all paths, provenance and commands. Seven assets are verified by a SHA-256 manifest: a three-page vector PDF, scan-like raster PDF, extreme-dimension PDF, 32-second generated PCM WAV, MP3 encoding of the same original signal, and deliberately corrupt PDF/WAV files. Python standard-library code deterministically reproduces PDF/WAV bytes; optional MP3 regeneration uses ffmpeg/libmp3lame. `mise run check:fixtures` needs no encoder and also checks the committed MP3 hash.

The drawings are schematic staves and note marks; the tones are an independent original test signal. No existing song, choir recording, external score, licensed pilot corpus or human singing is included. These fixtures exercise software behavior, not choral/musical usefulness. Oversize streams and interrupted providers are generated in tests without committing large dummy files.

## Validation evidence

Final verification passed; [recorded command excerpts](evidence/sas-006/check-results.txt) preserve the successful build/lint/device outputs. Device tests run on KVM-accelerated x86_64 API 26 (Android 8.0, Pixel 2 profile) and API 36 (Android 16, Pixel 6 profile) emulators already prepared during SAS-022. A tablet-sized window is an emulator display override, not a physical-tablet claim.

| Check | Result |
| --- | --- |
| `mise run check:app` | Passed: seven fixture byte/hash checks, actual debug APK and Android Lint (`No issues found.`) |
| `ANDROID_SERIAL=emulator-5558 mise run test:app` | Passed: nine tests, zero failures/errors/skips on API 26 |
| `ANDROID_SERIAL=emulator-5556 mise run test:app` | Passed: nine tests, zero failures/errors/skips on API 36 |
| Actual system document picker | PDF and WAV chosen from Downloads; private metadata contained UUID names, expected source labels, three pages and 32000 ms duration |
| Source independence/offline/process restart | Deleted both original Downloads files, enabled airplane mode, force-stopped/relaunched: score/recording opened, playback advanced, page 2 and approximately 12-second checkpoint restored on a further cold start, paused |
| Actual display checks | [Phone portrait](evidence/sas-006/phone-portrait.png), [landscape](evidence/sas-006/phone-landscape.png), [tablet window](evidence/sas-006/tablet-window.png), [dark mode](evidence/sas-006/phone-dark.png); actual PDF pixels, selected page and audio controls visible; 2× zoom/pan exercised |
| Picker cancellation / bad-file UI / short window | Passed: cancellation preserved exact workspace; corrupt PDF chosen through picker showed an error and preserved prior data; [300×500 dp window / 1.5 font scale](evidence/sas-006/narrow-large-text.png) kept playback controls reachable through scrolling |
| `mise tasks validate`, dependency inspection | Seven valid task definitions; pinned feature dependencies inspected |
| Documentation / Git whitespace | Passed: 55 Markdown files; `git diff --check` clean |

The nine device tests cover empty launch/recreation; demo rendering, real playback/seek and independent page turns; paused relaunch and restored files/positions; actual backgrounding and permanent audio-focus loss; valid PDF/WAV import/decoding; corrupt/empty/interrupted import rollback; size caps and unsafe-path rejection; scan-like/extreme-page bounded rendering; MP3 validation with corrupt audio rollback; and cleanup/stale-position protection (some assertions share a test). Tests use actual parser/decoder/player behavior, not mocked success.

Headless emulator runs decode/render and assert playback progress; they do not prove loudspeaker/headphone audibility. Generated reports/APKs and SDK data remain local build output. Successful command excerpts and screenshots are retained with this report. No SingASong entry appeared in the API 36 crash buffer after validation. Local debug APK SHA-256: `11194b0b66dae80d534ea50b93ad9eda8dec4265efd8d084a132a9384d942556` (machine-local debug signing means rebuild hashes may differ). Emulator overrides were restored; test AVDs are retained locally and were stopped after validation. No Play publication, signing changes, cloud deployment, remote tracker changes, Git history rewrite or reference-repository mutation occurred.

## Remaining validation

SAS-003 retains real choir assets/permissions, scanned-score quality, musical usability, organiser/singer workflows and pilot devices. Reassess renderer fidelity, memory and minimum SDK on that corpus. SAS-008 owns the full persistent library/import UX; SAS-009 owns rehearsal playback/background controls and hardware interruptions. SAS-002/007/015 retain Console, release/CI and visual identity work. These are explicit future gates, not claimed completed by synthetic acceptance.
