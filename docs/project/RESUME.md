# Resume here — 2026-10-31 library checkpoint

The maintainer stopped work to conserve model credits. No new implementation should run until resumed. Sol completed the first SAS-008 slice and is idle; the parent remains In progress, paused between slices at the maintainer's request.

## Repository and completed work

- Working directory: `/home/henri/IdeaProjects/sing-a-song`.
- Branch: `sas-001-planning-tracker`.
- Origin: `git@github.com:hmdebenque/SingASong.git`, now owned by the maintainer. Reset/restructuring is authorized if useful; none is needed to resume. No reset or push performed.
- Local [backlog](backlog.md) is authoritative; remote tracker management is not required.
- SAS-001 integration, SAS-022 Android modernization and SAS-006 prototype are Done.
- SAS-008 first slice implemented: SQLite songs/arrangements/resources, editable metadata, normalized search, multiple labelled PDFs/audio, per-resource positions, safe prototype conversion. See [implementation evidence](sas-008-library-evidence.md) before changing it.
- Sol recorded passing build/lint/fixtures plus 19 tests on each API 26 and Pixel 7 Pro/API 36, and manual picker/offline/restart/keyboard/rotation/large-text/window checks. These results were not rerun at wrap-up; documentation, fixtures and Git whitespace were rechecked.
- Generated material only; real choir corpus/physical-device validation remains SAS-003. The app has never been published and has no legacy users.

## Next bounded work

Start with the next SAS-008 slice: arrangement-owned plain lyrics (entry/import/edit/read with adjustable text, and search). Prepare acceptance criteria and hand it to Sol or a suitable Claude model using the provider-neutral workflow. Preserve the existing library and reader/player behavior. Then implement preferred voice-part defaulting/filtering, followed by safe local backup/restore. Keep the full parent open until its remaining acceptance is met; do not claim real choir validation from fixtures.

SAS-002 unpublished Play configuration and SAS-003 real material/workflow collection remain open. No backend, paid hosting, publication or remote push is implied by resumption.

## Sessions and validation

- Sol Codex session ID: `01a19321-f924-7780-9251-2222a82b09f0` (can resume with `codex resume` using this ID).
- Orca terminal handle at checkpoint: `term_fc7fde20-1fd1-4cf9-855b-cde8478032dc`, tab SAS-008 · Sol. Handles may change after Orca restart; re-list before sending.
- Orca workspace: `fa47b814-1c6f-473c-bfbc-691891b009cf::/home/henri/IdeaProjects/sing-a-song/`.
- Use the orca-cli skill for handoffs; CLI resolved here as `orca-ide`. User prefers uninterrupted local execution, with routine implementation/setup decisions made autonomously. Previous Sol launch used high reasoning, danger-full-access, approval never. Preserve boundaries around secrets, paid services and publication.
- Existing AVDs: `singasong-pixel7pro-api36` and `sas022-api26`. The task emulators are stopped; an unrelated Medium Phone emulator was left untouched.
- Commands: `mise run check:app`, `ANDROID_SERIAL=<target> mise run test:app`, `python3 scripts/check_docs.py`, `git diff --check`. See [emulator guide](../guides/pixel-emulator.md).
- Backup of this checkpoint: `/home/henri/.local/share/sing-a-song/checkpoints/2026-10-31-library/` contains a verified Git bundle and the debug APK. Local backup only, not off-device storage.

Read AGENTS.md, the backlog, the SAS-008 evidence and current architecture before implementation. The last assistant message supplies the checkpoint commit hash; no reliance on conversation context is required.
