# SAS-001 integration evidence

Date: 2026-10-30. Task state and assignment are maintained only in the [backlog](backlog.md). Local integration and local tracking meet the revised acceptance scope; remote tracker management is no longer required by the maintainer.

## Repository and history

- Repository: [frenchfrie/SingASong](https://github.com/frenchfrie/SingASong), HTTPS origin `https://github.com/frenchfrie/SingASong.git`.
- Local branch: `sas-001-planning-tracker`, based directly on original default branch `master` at `26e83a75533d160fa636d61895cd48d76696b1d8`.
- All 29 original commits, including merge commits and both original root histories, are preserved. Clone is not shallow. No new root history, force-push or default-branch merge was made.
- All 95 original tracked paths remain present. The 94 paths other than README.md are byte-for-byte identical, including executable permissions, to the original tip. Legacy `app/`, package trees, root Gradle files, wrapper and test resources remain in their original locations. No legacy migration was performed.
- The only overlapping path was README.md. The original was exactly `# SingASong` followed by a newline. The planning README retains that title and adds the reboot plan, legacy location and accurate implementation status. Its original version also remains accessible through Git history.

## Backup and import procedure

Before importing any checkout, copied all 25 initial planning files into `/tmp/sing-a-song-sas001-backup-ho0ycd9h/planning/` with a SHA-256 manifest in the parent directory. An import guard then caught newer planning edits and stopped before importing. Those edits included provider-neutral capability and assignment guidance; they were retained.

Backed up all 25 current planning files again into `/tmp/sing-a-song-sas001-backup-pzpyac_u/planning/`, with `planning-sha256.json` in the parent. Verified each hash against the live workspace immediately before import. Staged the full original clone at `/tmp/sas001-original` and preserved a complete Git bundle at `/tmp/sas001-original-history.bundle`. These `/tmp` backups are local recovery material, not durable remote backups or repository content; retain/copy them before system cleanup if needed.

Verified that the workspace had no existing Git metadata and that README.md was the sole collision. Created the feature branch in the staged clone, copied every non-overlapping tracked file and the complete Git metadata into the existing folder, and retained the planning files. No initial commit or unrelated history was created. Updated the README, contributor/agent guidance, roadmap, component/development status, decision implementation notes and log to distinguish preserved legacy code from unimplemented reboot stacks. Decisions remain Proposed unless already accepted by the maintainer.

## Tracker access and preparation

Both the connected GitHub tool and the working local `gh` authentication identify `hmdebenque` with repository permissions `pull: true`, `push: false`, `triage: false`, `maintain: false`, `admin: false`. The initial sandbox authentication result was misleading because network access was blocked; authentication succeeded outside the network sandbox, but repository permissions remained pull-only.

The authorized attempt to create the `documentation` label using `POST /repos/frenchfrie/SingASong/labels` returned HTTP 404 (`Not Found`). No label was created. Tracker-management access is unavailable with the current credentials. Reauthentication alone is insufficient unless the new credential/account is granted suitable repository permissions.

Read the complete open/closed issue inventory, milestones and labels. Two open legacy issues exist; by explicit user instruction they are irrelevant to this work and remain untouched and excluded from the SAS tracker. No SAS issues or milestones existed. The [prepared tracker package](tracker/README.md) contains 21 independent SAS issue definitions, six milestone definitions, labels, dependency IDs, capability/review requirements and the exact configuration/verification procedure. No remote SAS issues, milestones, project, comments or code changes were created. No SAS issue/project links are claimed.

The maintainer subsequently removed GitHub tracker management from acceptance on 2026-10-30. No remote tracker setup or authority transfer is required. The backlog retains all stable SAS IDs, milestones, dependencies and completion evidence and is the sole task-status/assignment authority. The earlier access failure is historical evidence, not an outstanding blocker. The prepared import package is archived.

## Validation

- `git fsck --full`: passed.
- `git bundle verify /tmp/sas001-original-history.bundle`: passed; complete original history recorded.
- Original commit-set, ancestry, tracked blob and file-mode comparisons: passed; all 29 original commits and 95 paths retained, with README.md the sole deliberate content change.
- Planning backup hashes: passed before import; all latest planning files were retained before the deliberate documentation reconciliation.
- Tracker specification validation: 21 unique stable IDs, six known milestones, defined labels, existing body files and valid acyclic dependencies; acceptance/capability/review content matches the local planning definitions.
- `python3 scripts/check_docs.py`: passed.
- `git diff --check` and staged equivalent: passed.

No legacy Android build, device tests or reboot component tests were run: this task changes planning and repository integration only. Legacy build/upgrade viability remains unaudited under SAS-002/006. No app was published and no cloud services deployed.

## Subsequent maintainer-directed build relocation

After the original integration above, the maintainer required all Android build files to live under `app/` and requested a root mise task. The wrapper, wrapper distribution settings and Gradle properties were moved there. The root buildscript/repositories and application module configuration were combined into `app/build.gradle`, with `app/settings.gradle` naming a single root project. The Android plugin supplies the root clean task. Source, resources, dependencies, application ID and version configuration are unchanged. The original 95-path preservation evidence above describes the initial integration; subsequent relocation intentionally changes those build-file paths. History still retains every original version.

Build-layout verification: mise task validation and dry run passed; the actual `mise run build:app` launched the moved wrapper and downloaded Gradle 4.4, then failed before project evaluation with `Could not determine java version from '21.0.12.1'`. No APK was generated. The legacy JDK/SDK compatibility work remains under SAS-006.

## Subsequent mise environment setup

The maintainer requested mise-managed prerequisites. The configuration now pins Temurin Java 8 for Gradle, Android SDK command-line tools 13.0, and task-scoped Temurin Java 17 for `setup:app`. Java runtimes and SDK tools installed successfully. The wrapper starts Gradle 4.4 on managed Java 8. The SDK installer reached its interactive license prompt, and a Gradle build check passed dependency resolution and stopped at missing SDK package licenses. SDK license acceptance and package installation are pending; no APK build is claimed.

## Closure under revised maintainer scope

On 2026-10-30 the maintainer authorized closure and stated that GitHub tracker management did not matter because it was nearly unused. Revised acceptance retains history/source preservation, deliberate planning reconciliation, stable SAS task IDs and one clear task-status authority; remote issue creation, issue links and a GitHub project are excluded.

Evidence: the original 29 commits remain ancestors of the integration branch; legacy source/resources are retained; README reconciliation and later explicitly requested Gradle relocation are documented above. The local backlog contains all 21 stable SAS IDs, dependencies, milestones, capability guidance and linked completion evidence. No old issues were modified. Documentation and Git whitespace/integrity checks pass. No default-branch merge, force-push, app publication or cloud deployment occurred.

The root mise environment is configured and Gradle starts on Java 8. SDK license acceptance, installation of the licensed SDK packages and APK/device verification remain Android build follow-up work under SAS-006. They do not block repository/planning integration. No APK success or product implementation is claimed by SAS-001 closure.

The waiting SDK installer was stopped without accepting the license. Resume that independent setup with `mise run setup:app` when the license can be reviewed and accepted.
