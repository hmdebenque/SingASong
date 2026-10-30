# Delivery workflow

## Milestones and sources of truth

The [roadmap](../../ROADMAP.md) owns scope and detailed gates. The [backlog](backlog.md) owns task state and assignment. Decision records own agreements; release records own delivery evidence.

| Milestone | Roadmap coverage | Outcome |
| --- | --- | --- |
| M0 — Foundation decisions | Phase 0, B0, P0 | Real-content slice, existing-app audit and architecture/scope decisions |
| M1 — Read and practise | Phases 1–2, B1, P1–P2 preparation | Offline library/reader/audio; public website foundation; internal build |
| M2 — Choir MVP release | Phase 3, P1–P3 | Packs/programmes and completed public-release gates |
| M3 — Practice improvements | Phase 4 | Prioritized reader/practice improvements |
| M4 — Hosted choir workspace | Phase 5, B2–B3 | Private shared repertoire and operational backend |
| M5 — Broader music support | Phase 6 | Validated structured notation and instrument expansion |

If hosted updates are required for MVP, move M4 tasks into M2 and make them explicit release dependencies. Website privacy/support availability belongs to M2 even when accounts are deferred.

## Task lifecycle

Use Backlog → Ready → In progress → Review → Done; use Blocked with a specific dependency and unblock condition. Ready means scope, acceptance, dependencies and relevant decisions are sufficient to start. Do not mark an unresolved backend choice as an implementation requirement.

Every task has a stable SAS-NNN ID, primary component, milestone, priority, dependencies, capability level, review needs, escalation trigger, owner/model/session when assigned, acceptance criteria and validation evidence. P0 blocks its milestone; P1 is required within that milestone but sequencing is flexible; P2 is optional/future. A task can be split, preserving its ID as a parent and linking new IDs. Never reuse IDs.

Keep one task per independently reviewable outcome. Cross-component features use a parent task and linked component tasks when they cannot fit into one coherent change. Avoid simultaneous changes to the same contract without coordination.

## Agent capability and assignment

Use provider-neutral capability levels to describe task needs. These are workload categories, not fixed rankings or guarantees about a model:

| Capability | Typical scope |
| --- | --- |
| Routine | Mechanical edits, documentation maintenance and straightforward configuration |
| Standard | Bounded implementation, tests, CI and repository integration |
| Deep reasoning | Architecture trade-offs, complex synchronization, security-sensitive design and difficult migrations |

The task definition records capability, review needs and a concrete escalation trigger. An optional suggested model is guidance only: Sol or an appropriate Claude model may handle Standard work; Astra or an appropriate Claude model may handle Deep reasoning work. Select from available models at assignment time according to task fit and budget; do not permanently bind capability levels to provider/model names.

Record the actual provider/model, reasoning setting when applicable, owner and session/worktree reference when assigning work. Human contributors can use the same tasks with model/session marked not applicable. Assignment is not proof that execution started or acceptance passed.

Escalate when a stated trigger occurs, such as ambiguous migration requirements, unresolved authorization boundaries or repeated unsuccessful attempts with no new evidence. Preserve findings and attempted checks in the handoff. Do not automatically spend more on every task or silently broaden scope. Split routine implementation from difficult design work when useful.

Review needs name the evidence and expertise needed, not a mandatory second agent. Human approval remains necessary for product/architecture decisions where the workflow calls for it; model capability does not confer decision authority. Reassess capability when a task is split or its scope changes.

## Local tracking and optional GitHub mapping

The [backlog](backlog.md) is the sole task-status and assignment authority. On 2026-10-30 the maintainer explicitly removed GitHub tracker management from SAS-001's required scope because the existing tracker was nearly unused. Keep stable SAS IDs, milestones, priorities, dependencies and linked completion evidence locally. GitHub issue numbers and remote issue links are not required for local task completion. Existing legacy issues remain untouched and outside this work.

The [archived tracker package](tracker/README.md) preserves the earlier import proposal. Do not apply it or maintain live task status there. GitHub access limitations are historical findings, not a project blocker. If the maintainer later requests remote tracking, reconcile the package with current tasks and explicitly agree the authority transfer before maintaining remote status; never keep two independent live status ledgers.

## Completion and release

A task is Done when its observable acceptance criteria pass, relevant tests/checks run, implementation/documentation agree, and evidence is linked. For code changes include appropriate failure cases and migration/compatibility checks. A deployment or legal/store task needs its own evidence, not merely merged code. Record missing prerequisites rather than claiming success.

A milestone closes when all required tasks and roadmap gates pass. Use the [release record template](release-template.md) for actual releases. Store private credentials, legal personal details and reviewer secrets outside the repository; link their secure location rather than copying them.

Review the backlog at each completed slice or pilot rehearsal. Update scope and dependencies when feedback changes the plan. Maintainer owns prioritization and release decisions; implementation owners are assigned when work starts.
