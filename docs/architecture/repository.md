# Repository ownership and interfaces

The Android application belongs in `app/`, as explicitly confirmed by the maintainer on 2026-10-30. This includes future reboot work. All Gradle configuration and the wrapper are inside `app/`; the repository root provides the mise task `build:app`. The former Android module is now the single Gradle root project, with source still in `app/src/`. The remaining layout is proposed, with component scope READMEs; add implementation folders when work starts.

| Owner | Owns | Boundary |
| --- | --- | --- |
| app | Kotlin app, Gradle wrapper, local persistence/migrations, offline media and Android release configuration | Never holds backend credentials; consumes backend contracts |
| web/website | Public pages, support, privacy/deletion entry points, static assets and web build | No private choir portal implied; no duplicate backend business logic |
| services/backend | Domain/API, authentication integration, authorization, metadata persistence/migrations, media access and service infrastructure | Authoritative server contracts; Android/web never access its database directly |
| infra | Shared modules, environment composition, common DNS/identity/network resources where applicable | Provider and IaC remain undecided; compose component-owned definitions |
| docs | Product scope, tasks, decisions, legal preparation and design source context | Published legal copy must identify its reviewed version |
| .github | Contributor templates and future build/release workflows | Credentials in protected CI configuration, never in repository files |

## Interface and data ownership

Backend interfaces will live under `services/backend/contracts/`; choose concrete OpenAPI/schema tooling during bootstrap. Consumers generate clients from the authoritative source or use it directly. Generated output must identify its source/version and regeneration command. Contract changes require consumer compatibility checks.

Portable concert-pack schemas have a distinct, cross-component lifecycle: place their authoritative definitions under `contracts/concert-pack/` when SAS-010 starts. Document schema versions, validation, compatibility and migration. Do not use the backend database schema as a file interchange contract.

Android owns on-device migrations and cache/read-state rules. Backend owns server migrations and deployment ordering. Shared language libraries are introduced only for actual shared code; do not force Kotlin, TypeScript and the backend language into one build ecosystem.

## Infrastructure and releases

Create component `infra/` definitions when they own resources and optional `deploy/` files when the selected runtime needs them. Root `infra/modules/` provides reusable building blocks; `infra/environments/` composes development, staging and production. The website may own static-hosting resources under `web/website/infra/`.

Android releases retain Play package/signing continuity; website and backend deployments have their own artifact versions. A release record links compatible app/backend/contract versions, schema changes and rollback or forward-fix plans. Backend APIs must support installed app versions according to a documented compatibility window.
