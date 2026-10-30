# Initial backlog

This file is the single task-status and assignment authority until GitHub tracker configuration succeeds. All entries below are Backlog, unassigned and unimplemented unless the current assignments section states otherwise. The [tracker package](tracker/README.md) is preparation material, not a second status ledger. Dependency IDs must be completed before dependent implementation, except planning that can proceed independently. Milestones and priorities follow the [workflow](workflow.md). Expand a row with the task template before starting substantial work.

| ID | Deliverable | Component | Milestone | Priority | Dependencies | Acceptance / evidence required |
| --- | --- | --- | --- | --- | --- | --- |
| SAS-001 | Integrate planning with original repository/history and configure tracker | documentation | M0 | P0 | — | Existing history preserved; docs reconciled; issue IDs/links and tracker authority recorded; no accidental replacement of legacy source |
| SAS-002 | Audit existing Play app and migration path | release/android | M0 | P0 | — | Package, signing access, version code, policy status and legacy-data upgrade strategy documented without secrets |
| SAS-003 | Collect pilot corpus and validate choir workflows | android/product | M0 | P0 | — | Representative PDFs/audio/arrangements available privately; sharing and device needs recorded; sample rights established |
| SAS-004 | Select backend, website hosting and hosted-MVP scope | backend/infrastructure | M0 | P0 | SAS-003 | Decision compares Firebase/AWS with costs, regions, identity and operations; maintainer choice recorded; dependent milestone assignments updated |
| SAS-005 | Establish data inventory and legal/content requirements | legal | M0 | P0 | SAS-003 | Data/SDK inventory, audience, content permissions and policy deliverables identified; no claim of legal clearance |
| SAS-006 | Android build foundation and single-song reader/audio spike | android | M0 | P0 | SAS-001, SAS-002, SAS-003 | Kotlin build and checks reproducible; real PDF/audio work offline on representative phone/tablet; renderer choice and limits recorded |
| SAS-007 | Delivery tooling, CI and environment plan | infrastructure | M1 | P0 | SAS-001, SAS-004, SAS-006 | Versioned tools, real component checks, protected signing/deployment credentials and reproducible internal artifact; no placeholder passing tests |
| SAS-008 | Offline song library and reader | android | M1 | P0 | SAS-006 | Import/search/arrangements/lyrics/page state pass Phase 1 gate and failure scenarios |
| SAS-009 | Rehearsal recording playback | android | M1 | P0 | SAS-008 | Part selection, background controls, speed and A–B loop pass Phase 2 gate including interruptions |
| SAS-010 | Programmes and portable concert packs | android/contracts | M2 | P0 | SAS-008, SAS-009, SAS-004 | Versioned pack schema; another device imports and reads/listens offline; repeat/conflicting/interrupted imports handled |
| SAS-011 | Public website foundation and support hosting | website | M1 | P1 | SAS-004, SAS-005 | Chosen stack/build documented; accessible responsive site on HTTPS staging; public-page publishing and support route validated |
| SAS-012 | Hosted environment, identity and private storage | backend/infrastructure | M4 | P0 | SAS-004, SAS-007 | IaC/staging, auth, private media, budget alerts and backup/restore established; B1/B2 scope identified |
| SAS-013 | Choir membership, programme publication and synchronization | backend/android | M4 | P0 | SAS-012, SAS-010 | Versioned contract; two-choir isolation tests; interrupted sync and revocation scenarios pass; downloaded repertoire remains usable |
| SAS-014 | Account/data lifecycle and hosted-content controls | backend/website/legal | M4 | P0 | SAS-005, SAS-011, SAS-013 | In-app/web deletion, application-data cleanup, retention/ownership and applicable reporting controls exercised |
| SAS-015 | Visual identity and store asset kit | design/website/android | M1 | P1 | SAS-003 | Reviewed identity, editable icon/artwork, attribution and phone/tablet screenshot plan; no unshipped capability claims |
| SAS-016 | Publish legal pages and complete Console declarations | legal/website/release | M2 | P0 | SAS-005, SAS-011, SAS-015 | Public privacy/support URLs and build-specific Data safety/content declarations reviewed; deletion dependencies included if accounts ship |
| SAS-017 | Internal/closed choir pilot and release candidate | android/release | M2 | P0 | SAS-002, SAS-007, SAS-010, SAS-015 | Signed upgrade tested; two rehearsals and offline programme run; failures resolved; final store screenshots captured |
| SAS-018 | Store submission and controlled production rollout | release | M2 | P0 | SAS-016, SAS-017 | Review/app access complete; release evidence, monitoring and forward-fix plan ready; approved release rolled out under agreed controls |
| SAS-019 | Personal annotations and performance improvements | android | M3 | P1 | SAS-017 | Pilot-prioritized subset scoped; marks tied to score revision; replacement preserves previous annotations |
| SAS-020 | Hosted operations and launch verification | infrastructure/backend | M4 | P0 | SAS-013, SAS-014 | Restore, outage, tenant isolation, deletion, cost and compatibility checks pass; production runbook recorded |
| SAS-021 | Structured notation feasibility and instrument plan | android/backend | M5 | P2 | SAS-017 | Real scores evaluated for fidelity/playback/licensing; supported subset and go/no-go decision documented |

If SAS-004 selects hosted MVP, SAS-012/013/014/020 move to M2; SAS-016 and SAS-017 additionally depend on SAS-014, and SAS-018 additionally depends on SAS-020. Adjust pack scope if live sharing replaces it, while preserving export/backup requirements. Parent rows must be split into bounded tasks before implementation when they cover multiple independently reviewable outcomes.

Initial project-management scaffolding is recorded in the [log](../log.md); it does not complete any product/backend/release task above.

## Capability and review guidance

These are initial workload assessments, revisited when parent tasks are split. Model suggestions and assignments follow the [workflow](workflow.md); no model/provider is required for unassigned work.

| Task | Capability | Review needs | Escalation trigger |
| --- | --- | --- | --- |
| SAS-001 | Standard | Preserved history/source, reconciled docs and tracker consistency | Ambiguous history reconciliation or destructive migration appears necessary |
| SAS-002 | Deep reasoning | Signing continuity and actual installed-app upgrade evidence | Missing signing access or uncertain data compatibility |
| SAS-003 | Standard | Maintainer/choir validation of corpus, permissions and workflows | Missing rights or conflicting pilot requirements |
| SAS-004 | Deep reasoning | Maintainer architecture decision; cost and operations comparison | Unresolved scope, identity or provider trade-offs |
| SAS-005 | Deep reasoning | Publisher review; qualified legal input where needed | Unclear data-processing roles, rights or applicable obligations |
| SAS-006 | Standard | Reproducible build and real-device reader/audio results | Renderer/device limitations require architectural changes |
| SAS-007 | Standard | CI credential boundaries and reproducible release artifacts | Unclear production access or signing isolation |
| SAS-008 | Standard | Persistence/import failure cases and offline usability | Data loss or conflicting migration requirements |
| SAS-009 | Standard | Audio focus, interruption and loop/device behaviour | Persistent timing or platform lifecycle failures |
| SAS-010 | Deep reasoning | Pack compatibility, safe import and revision integrity | Ambiguous conflict semantics or unsafe archive handling |
| SAS-011 | Standard | Accessibility, published links and hosting configuration | Hosting/authentication scope expands beyond public pages |
| SAS-012 | Deep reasoning | IAM/private storage, IaC and restore evidence | Tenant boundary or recovery design remains unresolved |
| SAS-013 | Deep reasoning | Cross-choir isolation, revisions and offline sync correctness | Conflicting edits, revocation or retry semantics unresolved |
| SAS-014 | Deep reasoning | Deletion/retention and ownership scenarios; publisher/legal review | Deletion obligations conflict with shared ownership or retention |
| SAS-015 | Standard | Maintainer visual review, accessibility and asset provenance | Brand direction or usage rights remain unresolved |
| SAS-016 | Standard | Publisher review of policies and actual build disclosures | New data flows or policy ambiguity needs specialist assessment |
| SAS-017 | Standard | Device/pilot results, signed upgrade and regression evidence | Data-loss regression or repeated device-specific failures |
| SAS-018 | Standard | Release owner verifies store readiness and recovery controls | Policy rejection, unexpected rollout failures or signing issue |
| SAS-019 | Deep reasoning | Annotation persistence and score-revision compatibility | Marks cannot be preserved safely across revisions |
| SAS-020 | Deep reasoning | Restore/outage drills, isolation and operational readiness | Recovery goals or compatibility guarantees fail |
| SAS-021 | Deep reasoning | Real-score fidelity, licensing and supported-subset decision | Engine limitations prevent correct representative playback |

## Current assignments

- **SAS-001:** assigned to OpenAI GPT-6.1 Sol, medium reasoning, in Orca tab `SAS-001 · Sol`.
- **Session:** `term_0092226f-88ca-4e6d-a6f5-d911f1beee59`.
- **Workspace:** `fa47b814-1c6f-473c-bfbc-691891b009cf::/home/henri/IdeaProjects/sing-a-song/`.
- **Handoff evidence:** Orca accepted prompt request `74e97311-822f-43f6-af17-8c9d4aebd9a5` on 2026-10-30. Local execution is verified in the [integration report](sas-001-integration.md); tracker acceptance remains unmet.
- **SAS-001 status: Blocked.** Local history/source integration and planning reconciliation are complete. GitHub account `hmdebenque` has pull access only; label creation returned HTTP 404. Unblock with a credential/connection permitted to manage labels, milestones and the intended tracker in `frenchfrie/SingASong`, then apply the prepared package, record real SAS issue links and verify configuration before marking Done.
- All other tasks remain unassigned. Transfer these fields into authoritative issues when SAS-001 configures the tracker; do not maintain a second live assignment ledger here afterward.
