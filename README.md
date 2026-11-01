# SingASong

An Android Kotlin app for choir repertoire: find music, read comfortably, practise voice parts and prepare shared concert programmes. Guitar, piano and structured score playback follow the choir MVP.

## Project status

The current owned repository is [hmdebenque/SingASong](https://github.com/hmdebenque/SingASong). The maintainer authorized restructuring/resetting it for the reboot; see [repository ownership](docs/project/repository-ownership.md). No reset or remote write was needed for this slice. The original [frenchfrie/SingASong](https://github.com/frenchfrie/SingASong) history remains available as reference. SAS-022 implements a minimal Kotlin AndroidX Compose/Material 3 foundation in `app/`, including its Gradle build and wrapper. Build from the root with `mise run build:app`; see [Android setup and checks](app/README.md). SAS-008 extends the SAS-006 reader/player with a persistent offline song library: editable title/composer/tags, arrangements, labelled PDF/recording resources, case/accent-insensitive search and resource-specific saved positions. Existing SAS-006 content is safely converted. See the [implementation and validation evidence](docs/project/sas-008-library-evidence.md). Lyrics, preferred-part/filtering, backup/restore and programmes remain future work; website, backend and infrastructure remain unimplemented. A Play developer account/app already exists, but its configuration has not been audited.

Repository integration is recorded in the [SAS-001 integration report](docs/project/sas-001-integration.md). The [local backlog](docs/project/backlog.md) is the task-status authority; GitHub tracker management is not required by the maintainer.

## Start here

- [Roadmap](ROADMAP.md): product phases, backend work and Play/legal/design release gates.
- [Documentation index](docs/index.md): decisions, ownership and guides.
- [Delivery workflow](docs/project/workflow.md): milestones, task states and completion criteria.
- [Initial backlog](docs/project/backlog.md): bounded work and dependencies.
- [Decision index](docs/decisions/README.md): confirmed constraints and open choices.
- [Contributor guide](CONTRIBUTING.md) and [agent guidance](AGENTS.md).

## Repository ownership

```text
app/                Android application and its build/release configuration
web/website/        Public website, support and published legal pages
services/backend/   Backend domain, owned API contracts, persistence and resources
infra/              Shared infrastructure modules and environment composition
docs/               Project management, decisions, architecture, legal and design
scripts/            Repository-wide development and validation helpers
.github/            Issue/PR templates; future CI and release orchestration
```

The Android application belongs in `app/`, including future reboot work. The other directories describe the proposed component layout. Each component contains a scope README; the Android foundation is implemented. Create implementation subdirectories when needed. See [ownership and interfaces](docs/architecture/repository.md) for the planned layout. An organiser web portal, if needed later, will be a separate application under `web/`; it is not part of the public website.

## Current validation

Run `mise run check:app` to assemble the debug APK and run Android Lint. Run `mise run test:app` on a connected device/emulator for import, PDF/audio, playback and lifecycle tests. Run `mise run check:docs` (or `python3 scripts/check_docs.py`) and `git diff --check` for repository checks. The direct Python check needs only Python 3. See the [development guide](docs/guides/development.md) and [SAS-022 evidence](docs/project/sas-022-modernization.md).

## Modern Android foundation

The app was never published and has no users; legacy user-data migration is unnecessary. SAS-022 replaces the obsolete framework/toolchain, preserving history and application ID `com.frenchfrie.singasong`. The [report](docs/project/sas-022-modernization.md) records exact compatible pins, source disposition, SDK license/setup outcome and build/device evidence. SAS-006 supplied the [offline technical prototype](docs/project/sas-006-prototype.md), now extended by the first SAS-008 library slice; real choir corpus/pilot validation remains SAS-003. [Android architecture](docs/architecture/android.md) records the current structure and useful patterns from the maintainer-directed `venez-diner` reference.
