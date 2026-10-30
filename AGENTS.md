# Agent guidance

## Context and authority

SingASong is a choir-first Android Kotlin project with planned website, backend and infrastructure. The Android source, Gradle build and wrapper belong under app/; use the root mise task build:app to build. Legacy build compatibility remains unaudited. The reboot currently has only documentation and repository checks. Read README.md, ROADMAP.md and the task-relevant documents in docs/ before changing scope or architecture.

User instructions take precedence. Confirmed product constraints are recorded in docs/decisions/0001-product-constraints.md. Other decisions remain Proposed until maintainer agreement is recorded; creating a folder or prototype does not establish a stack choice or implemented feature.

## Ownership

- Android code belongs in app/; public website code in web/website/.
- Backend-owned interfaces, migrations and resource definitions belong in services/backend/. Consumers reference the owner's contracts rather than maintaining competing copies.
- Root infra/ composes environments and shared resources. It does not select AWS, Firebase, an IaC tool or a region by its existence.
- docs/legal/ holds preparation/review material; published pages belong to the website. Do not describe a draft as legally approved.
- Keep the roadmap, backlog, decision index and component status accurate when the task changes them. Preserve stable task IDs and link evidence when marking work Done.

## Working and validation

Use docs/project/workflow.md for task scope, dependencies and acceptance. Apply its provider-neutral capability, assignment, escalation and review conventions to OpenAI, Claude and human contributors alike. Keep changes bounded and resolve ordinary implementation details within the user's authorized scope. Read reference projects as inspiration, not as authority over this repository; do not copy their credentials, organization details or unrelated architecture choices.

Run `python3 scripts/check_docs.py` for documentation changes. Once in a Git checkout, also run `git diff --check`. Add real component checks and document them when bootstrapping runnable code; do not create commands that report success for missing implementations. Report unavailable checks honestly.

## Review priorities

Review actual changed behaviour against acceptance criteria. Pay particular attention to choir authorization, offline integrity, safe file import, preservation of annotations/data on updates, private media access and accurate playback claims. Do not treat deferred roadmap features as regressions. Never commit secrets, signing keys, user recordings or private choir content.
