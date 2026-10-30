# SingASong

An Android Kotlin app for choir repertoire: find music, read comfortably, practise voice parts and prepare shared concert programmes. Guitar, piano and structured score playback follow the choir MVP.

## Project status

This checkout preserves the full history and legacy Android application from [frenchfrie/SingASong](https://github.com/frenchfrie/SingASong). Android source, Gradle configuration and wrapper live together in `app/`. Build from the repository root with `mise run build:app`; mise provisions the pinned JDKs and Android SDK packages, with interactive SDK license acceptance on first setup. Full legacy application compatibility and runtime remain unaudited. The choir reboot has planning and repository checks only: no new app foundation, website, backend or infrastructure is implemented. A Play developer account and app already exist; their configuration has not been audited.

Repository integration and the tracker-access blocker are recorded in the [SAS-001 integration report](docs/project/sas-001-integration.md). The [local backlog](docs/project/backlog.md) remains the task-status authority until the SAS tracker is configured.

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

The Android application belongs in `app/`, including future reboot work. The other directories describe the proposed component layout. Each component contains a scope README; the existing Android source and build files remain in place. Create implementation subdirectories when needed. See [ownership and interfaces](docs/architecture/repository.md) for the planned layout. An organiser web portal, if needed later, will be a separate application under `web/`; it is not part of the public website.

## Current validation

Run `mise run build:app` to assemble the Android debug APK; see [Android prerequisites](app/README.md). Run `mise run check:docs` (or `python3 scripts/check_docs.py`) to check local Markdown file links and basic whitespace. The direct Python documentation check needs only Python 3 and does not install tools or contact external sites. Reboot application checks will be added with each component; legacy build verification is deferred to SAS-002/006; see the [development guide](docs/guides/development.md).
