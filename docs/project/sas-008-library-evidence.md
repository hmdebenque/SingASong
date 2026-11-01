# SAS-008 library implementation evidence

## Implementation proposal — 2026-10-31

Sol (OpenAI GPT-6.1 Sol, high reasoning), session `term_fc7fde20-1fd1-4cf9-855b-cde8478032dc`, accepted the maintainer-authorized full ownership handoff in the existing `sas-001-planning-tracker` checkout at `f0cc0d7`. Existing pilot/Pixel and marketing/audio edits are preserved. [Scope](sas-008-library.md); [assignment](backlog.md).

Use platform `SQLiteOpenHelper`, schema version 1, with foreign keys, explicit transactions and IO-thread repository operations. No new dependency or tooling change: [SQLiteOpenHelper](https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper), [SQLiteDatabase](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase) and [Normalizer](https://developer.android.com/reference/java/text/Normalizer) are available below minimum API 26. Room would add compiler/plugin integration without being necessary for this bounded local catalogue; revisit it if queries/schema complexity grows. Unsupported database upgrades fail rather than deleting data.

Songs have UUID identity, title, composer and comma-separated tags. Arrangements reference songs, with distinct names, arranger and flexible voicing. Resources reference arrangements, with UUID identity, PDF/audio kind, label, optional part, private filename and validated page count/duration. A separate resource-state table stores page, fit-width/2× zoom and recording position. Selection records remember arrangement per song and resources per arrangement. Foreign-key indexes support ownership lookups; normalized substring search scans the local repertoire using Unicode decomposition, combining-mark removal and locale-independent lowercase. Display spelling is retained. No FTS dependency or token-prefix restriction is needed for the first local library.

Imports retain the 20 MB/300-page PDF and 64 MB recording bounds. Stage, sync and validate before moving to a UUID private filename and committing metadata. Failure removes only the attempt's uncommitted files; existing resources are never replaced. No deletion UI or broad orphan sweep. A killed process can leave an unreferenced file; retain it rather than risk data loss. The existing isolated PDF service and foreground Media3 player remain.

Convert SAS-006 by reading its atomic JSON without invoking its orphan cleanup, copying referenced files into the library and committing song/arrangement/resources/positions plus a conversion marker in one database transaction. Originals remain untouched. Missing or malformed legacy data blocks conversion with an actionable error and permits retry; no destructive fallback. Empty legacy metadata can be marked converted without creating a song. Reader jobs are canceled/identity-checked, and all position writes carry the originating resource IDs. Switching pauses and checkpoints before changing selection.

## Results

First library slice implemented and validated locally; parent SAS-008 remains In progress. This first slice does not complete SAS-008 or Phase 1. Lyrics, preferred-part behavior/filtering and local backup/restore remain required; SAS-003 real choir/device validation remains open.

## Implemented slice

- Platform SQLite catalogue with UUID songs/arrangements/resources, foreign keys, ownership indexes and separate personal state/selections. Song forms validate blank/oversized titles and support cancel; arrangement names must be distinct under the song, including normalized case/accent equivalents. Original title/composer/tags/arranger spelling is retained. Duplicate song titles are valid.
- Search uses whitespace-separated normalized terms across title/composer/tags/arranger. Empty library, empty query and no-results states are distinct. Metadata editors/material selectors scroll with safe/IME insets; list/song navigation needs no new navigation dependency.
- **Materials** groups arrangement choices, PDF scores and recordings. Each import adds a resource with a required label and optional free-text part (SATB/custom). Full/part PDFs and tutti/part recordings coexist. The reader shows song, arrangement and selected resource labels. Resource labels/parts are entered at import; a resource-metadata editor is not included.
- Imports use the existing isolated PDF worker and Media3 decoder validation, bounded staging/private copies and a transactional commit. URI references are not kept. Re-importing a source deliberately adds another independent resource; content deduplication/replacement UI is not included. Failed imports clean only their own attempt. No deletion UI or blanket orphan cleanup exists. A killed process may leave an unreferenced private file; SAS-006 originals are retained for recovery. These retained files consume storage until a future deliberate cleanup design.
- ViewModel checkpoints carry originating IDs, page/zoom and independent recording time. Selection changes pause/checkpoint before loading; obsolete rendering is canceled and results identity-checked. Moving to a song/arrangement without media clears the old bitmap/player. Cold start never autoplays. Playback still pauses on actual app backgrounding and audio-focus loss; no background session, loop or speed claim.
- Conversion reads SAS-006 atomic JSON without its orphan cleanup, validates copies, and commits all converted metadata/positions and the conversion marker together. The marker is rechecked inside the transaction to prevent a second concurrent store from creating a duplicate song. Missing/corrupt inputs or rejected commits leave originals/current catalogue intact and expose retry. Unsupported schema versions fail without resetting the database.

## Validation — 2026-10-31

All material below is repository-generated. No real choir material, physical device, audible speaker/headset quality or full Phase 1 completion is claimed.

| Check | Actual result / retained evidence |
| --- | --- |
| Root `mise run build:app` | Debug APK assembled with unchanged stable pins; no dependency/tool installs needed. |
| Root `mise run check:app` | Fixture hash verification, assembleDebug and Android Lint passed. [Command output](evidence/sas-008/check-app.txt). |
| Pixel 7 Pro/API 36 | 19 instrumentation tests, zero failures/errors/skips. [Command](evidence/sas-008/pixel7pro-tests.txt), [JUnit results](evidence/sas-008/pixel7pro-results.xml). |
| API 26 (`sas022-api26`) | Same 19 instrumentation tests, zero failures/errors/skips. [Command](evidence/sas-008/api26-tests.txt), [JUnit results](evidence/sas-008/api26-results.xml). |
| Repository documentation/whitespace | `python3 scripts/check_docs.py` and `git diff --check` passed; [check output](evidence/sas-008/repository-checks.txt). |

The six UI/device tests cover empty launch/recreation, three song creations, blank-title validation, title/composer/tags/search, canceled edits, two arrangements, explicit alternate labelled PDF/audio selection, independent page/zoom/time, clearing old media, foreground playback, seek, focus loss and recreation/cold ViewModel restore. Seven library-store tests cover display-preserving normalized search, duplicate titles/distinct arrangements and owner validation, multi-resource private copies and per-resource state, removed sources, oversized/corrupt/interrupted/canceled imports, rejected resource commits, concurrent/idempotent conversion, missing/malformed legacy data, rejected conversion commit/retry and future-schema preservation. The six retained SAS-006 tests still exercise real parser/decoder behavior, render bounds, original import rollback and stale replacement-position protection.

Manual Pixel checks used the actual system document picker to add **Alto score · Alto** and **Alto rehearsal · Alto** alongside demo full-score/tutti resources. Canceled another picker without changing the four committed resources. Deleted both `/sdcard/Download/SAS008-generated.*` sources, enabled airplane mode, force-stopped and reopened: page 2/3, 2× zoom and approximately 5 seconds restored, paused, with both private resources usable. During the later wider-window check, advanced to page 3 and played/returned through Library; reopening retained page 3 and approximately 7 seconds, paused. Temporarily moved the private selected PDF out of its location: cold launch showed a missing-file warning without losing metadata or audio; restoring the same private file and reopening recovered page/zoom. No replacement or wiping was used.

| Visual / interaction check | Evidence |
| --- | --- |
| Phone reader, original generated resources | [Portrait](evidence/sas-008/pixel-reader.png) |
| Multiple labelled PDFs/recordings | [Materials](evidence/sas-008/pixel-materials.png) |
| Software keyboard with import fields/actions above it | [IME](evidence/sas-008/pixel-keyboard.png) |
| Offline cold restart after source removal | [Restored reader](evidence/sas-008/pixel-offline-restart.png) |
| Rotation retains page/zoom/time, wide two-pane reader | [Landscape](evidence/sas-008/pixel-landscape.png) |
| 200% font, keyboard open; scroll reaches Cancel/Save, Back returns to repertoire | [Scrolled editor](evidence/sas-008/pixel-large-text-editor.png) |
| 1600×2560, density 240 (about 1067 dp wide), list/song navigation and page/play controls | [Library](evidence/sas-008/large-window-library.png), [two-pane reader](evidence/sas-008/large-window-reader.png) |
| Missing selected private PDF reported explicitly | [Warning](evidence/sas-008/pixel-missing-file.png) |

Display overrides/font/rotation/radio settings were restored after the manual checks. Existing AVDs were reused without wipe/recreation. An unrelated running AVD was left untouched. Test-runner installation cleanup is expected; the updated APK was reinstalled for the final demonstration. The initial test launch raced emulator boot and failed with device offline; rerunning after `sys.boot_completed=1` succeeded. Initial lint found two unused replacement strings, removed before the final passing check. Initial test compilation had an unnecessary assertion import, removed before the passing runs.

## Remaining parent work and limits

SAS-008 stays **In progress**. This slice is delivered for review; it does not close Phase 1. Remaining parent acceptance:

1. Plain lyrics: entry/import/editing, adjustable-text reader and searching entered lyrics, grouped under arrangements.
2. Remember preferred SATB/custom part, default resource choice when available, availability filtering and explicit unavailable-part behavior. Current flexible labels/manual selection supply metadata, not this behavior.
3. Deliberate local backup/export and restore on a fresh installation, including library relationships, private resources and personal state; platform backup remains disabled. Include duplicate/conflict/missing-file and interrupted-restore recovery gates.
4. Complete the Phase 1 pilot-corpus/airplane-mode gate through SAS-003, with real choir documents, physical devices and singer/organiser feedback. Generated-fixture evidence cannot close it.

No automatic content deduplication, deletion/resource replacement/editor, annotations, continuous/fit-page reader, keep-awake setting, programme navigation or backend work was added. Reader scrolling/panning offsets are not saved; page and fit-width/2× mode are durable. Search scans a local snapshot rather than an FTS index; catalogue-scale performance needs real pilot evidence. Resource validation opens/decodes the existing bounded subset and does not promise that every later page/codec packet will succeed.

The [owned repository update](repository-ownership.md) supersedes historical pull-only/preservation design assumptions. Current active/uncommitted work was preserved; no reset, remote tracker update, push, publication, deployment, paid service or secret change was performed. The local backlog remains authoritative.
