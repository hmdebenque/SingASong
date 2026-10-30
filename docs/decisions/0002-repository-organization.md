# 0002: Repository organization and delivery conventions

- Status: Proposed
- Date: 2026-10-30
- Categories: cross-cutting
- Agreement/evidence: User requested a structure inspired by dsr-engine; detailed conventions are proposed here.

## Confirmed directory constraint

On 2026-10-30 the maintainer explicitly required the Android application to use `app/`. This applies to both preserved source and future reboot work, including all Gradle configuration and the wrapper. The maintainer also requested a root mise build task; `build:app` is the Android entry point. The broader conventions below remain Proposed.

## Proposal

Separate Android, website and backend workspaces; keep shared environment composition under infra/. Use a single documentation index, numbered decisions, stable task IDs, evidence-based release records and component-owned contracts/resources. See [repository ownership](../architecture/repository.md) and [workflow](../project/workflow.md).

## Consequences

Components can choose suitable languages and release independently while tracking shared workflow dependencies. Cross-component contract changes need compatibility checks. Avoid creating microservices or shared-language workspaces before there is a concrete need.

## Implementation status

Documentation directories, templates and a local documentation check exist. SAS-001 preserved the original history, `app/`, root Gradle build and wrapper without migrating legacy code. The subsequent maintainer-directed layout change put all Gradle files under `app/` and added the root mise task `build:app`; a build attempt is blocked by Gradle 4.4 rejecting Java 21. No reboot application manifests, new language toolchains, cloud configuration or automated publishing have been created. Tracker setup is blocked on repository-management access; see the [integration report](../project/sas-001-integration.md). The broader repository proposal remains Proposed.
