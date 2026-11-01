# SAS-008: Offline library — first implementation slice

Status/assignment: [backlog](backlog.md). Milestone M1, priority P0, capability Standard. Depends on completed SAS-006. The maintainer authorized this slice on 2026-10-31, including implementation by Sol using generated fixtures; SAS-003 real choir validation remains open.

## Observable outcome

Create and edit songs, browse/search a persistent repertoire, choose an arrangement and its score/recording, then read and practise in the existing offline reader. Opening a different song must not overwrite another song's files or saved positions.

## Data-model outline

Before coding, write a concise implementation proposal recording persistence, relationships, indexes/search normalization, migration and resource-lifecycle choices. Proceed with reasonable choices within this scope without waiting for a separate approval. Broad provider/product decisions remain outside it.

- Song: stable ID, nonblank title, optional composer and tags. Duplicate titles are legitimate; IDs identify records.
- Arrangement: stable ID and owning song, name, optional arranger and voicing. Create a sensible default arrangement for quick entry; permit multiple distinguishable arrangements per song.
- Resource: stable ID, owning arrangement, type (PDF or recording), display label, optional flexible voice-part label (SATB presets plus custom labels), private file identity and validated technical metadata. Support multiple resources of each type, explicitly selectable.
- Personal state: selected arrangement/resources, page/zoom as appropriate and independent recording position, keyed to resource identity. Keep resource content and navigation state distinct.

Choose durable local persistence appropriate to a multi-song library; Room is a candidate, not an already accepted dependency. Verify any added dependency/tooling compatibility against official docs and the existing stable matrix. Retain ViewModel/state/repository boundaries. No backend is needed for this task.

## Required slice acceptance

1. A user creates at least three songs, edits title/composer/tags, cancels an edit without changes, and sees validation for blank titles. Changes survive process restart offline.
2. Browsing and case/accent-insensitive search cover title, composer, tags and arranger. Search edits update results; empty-query, no-results and empty-library states are clear. Preserve original display spelling.
3. Two arrangements under the same song remain distinct. Attach/select full-score and part PDFs plus tutti/part recordings without replacing unrelated resources. Resource labels and arrangement identity remain apparent in the reader.
4. Reuse the isolated PDF reader and foreground Media3 playback. Reading, zoom, seeking, pause/background behavior and file-picker flow continue working. Switching songs/resources pauses previous playback, cancels obsolete work, and cannot save stale positions into the newly selected resource.
5. Files remain private copies and work after source removal, restart and airplane mode. Invalid, canceled, interrupted or oversized imports leave all previously committed library content usable. Handle missing files explicitly. Preserve existing bounds unless a documented requirement justifies a change.
6. Convert an existing SAS-006 workspace into a named library song/arrangement without losing its PDF/audio or saved positions. Conversion is idempotent and fails safely; no destructive database fallback or blanket orphan cleanup before the new metadata is durably committed. Although there are no legacy production users, the current prototype may already contain local test imports.
7. Generated demo content is explicitly labelled and does not silently populate or duplicate the library on every launch. Never commit private choir materials.
8. Validate meaningful persistence/search/relationship and migration/failure cases plus actual navigation/import/read/play behavior. Run build/lint/fixture/docs/whitespace checks and device tests on Pixel 7 Pro/API 36 and API 26. Check keyboard/insets, rotation, large text, back navigation and a larger window. Do not claim physical-device or real repertoire validation.

Deletion UI is optional for this first slice. If implemented, require deliberate confirmation and prove that removal cannot delete other resources; record retention/file cleanup semantics. Avoid implementing unrequested bulk-destructive actions to simplify tests.

## Boundaries and parent completion

This slice does not implement backend/accounts, programme sharing, chord notation, synthesis, annotations or SAS-009's background playback/speed/loops. Preserve the original broader SAS-008/Phase 1 scope: plain-lyrics reading/editing, preferred-part behavior/filtering and local backup/restore must be accounted for before marking the parent fully Done. Do not quietly drop them or claim a completed Phase 1 from this narrower library slice. On delivering the slice, keep SAS-008 In progress with exact remaining items and evidence, unless all parent acceptance is actually met under separately recorded scope.

## Ownership, evidence and execution

Sol owns implementation and reports directly to the maintainer. Preserve current uncommitted documentation and marketing/audio changes; inspect `git status` before editing. Use `venez-diner` read-only as architecture/design inspiration; do not inherit branding, secrets or versions blindly. Android build files remain under `app/`; use root mise tasks.

Record results in `docs/project/sas-008-library-evidence.md`, update current architecture/component status and the backlog, and retain useful test excerpts/screenshots without credentials or private content. Escalate only concrete uncertainty about destructive migration or unresolved data-integrity semantics. Ordinary local edits, tool/dependency installation and emulator verification are already authorized; no publishing, remote push, paid services, cloud deployment, secret changes or destructive Git operations.
