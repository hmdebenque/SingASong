# SAS-006: Single-song offline reader/audio prototype

Status and assignment: [backlog](backlog.md). Capability: Standard. Depends on SAS-022.

## Authorized scope

On 2026-10-31 the maintainer confirmed that pilot assets are unavailable and authorized proceeding with original generated fixtures. SAS-003 stays open for real choir material, workflow feedback and device inventory. It is a pilot-validation gate, not a prerequisite for this technical prototype. This change does not close Phase 0 or select backend/persistence/branding for the full product.

Implement one local practice workspace: import a PDF and a recording through Android's document picker, copy validated files to app-private storage, render/navigate/zoom the PDF while controlling foreground playback, and restore the selected files/page/audio position after restart. An explicit generated-demo action exercises the same ingestion path without needing user assets. Clearly label generated content. Keep PDF page and recording position independent.

## Acceptance

- Reproducible original multipage/vector, scan-like, oversized and corrupt PDF fixtures plus original synthesized WAV/MP3 recordings with provenance and generation instructions.
- Real PDF rendering and audio decoding/playback offline on phone and tablet-sized emulators; actual result on API 26 and API 36 documented.
- System document picker works; cancellation is harmless. Imports are size-bounded, use internal names, validate before committing, and leave the previous workspace usable after invalid/failed/interrupted imports.
- Single-song state is durable without retaining access to the source URI. Page and audio position survive activity recreation and a process restart; playback remains paused on cold start.
- Page navigation, fit-width zoom/scroll, play/pause, seek and visible duration/error state work. Audio focus/headphone handling and foreground lifecycle behavior are explicit; background playback, speed and A–B loops remain SAS-009.
- Choose/document renderer against minSdk, memory, isolation, licensing and fixture limitations. No promise that PDFs contain playable notes. No annotation, full library, concert packs or accounts.
- Meaningful build/lint/import/reader/playback/persistence tests and documentation/Git checks pass. No physical-device, real singing or pilot-corpus claim without evidence.

## Review and escalation

Review actual import rollback, private file handling, bounded PDF rendering, lifecycle/resource cleanup and truthful offline/playback claims. Escalate significant renderer/media architecture constraints. Record implementation evidence in `sas-006-prototype.md`; no mandatory second agent or remote tracker work.
