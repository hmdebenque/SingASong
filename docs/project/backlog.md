# Initial backlog

This file is the single task-status and assignment authority. The maintainer confirmed that GitHub tracker management is not required. All entries below are Backlog, unassigned and unimplemented unless the current assignments section states otherwise. The [archived tracker package](tracker/README.md) is historical preparation material, not a second status ledger or a required import. Dependency IDs must be completed before dependent implementation, except planning that can proceed independently. Milestones and priorities follow the [workflow](workflow.md). Expand a row with the task template before starting substantial work.

| ID | Deliverable | Component | Milestone | Priority | Dependencies | Acceptance / evidence required |
| --- | --- | --- | --- | --- | --- | --- |
| SAS-001 | Integrate planning with original repository/history and establish local tracking | documentation | M0 | P0 | — | Existing history preserved; docs reconciled; stable SAS IDs and local tracker authority recorded; no accidental replacement of legacy source; [verification evidence](sas-001-integration.md) |
| SAS-002 | Audit unpublished Play app configuration | release/android | M0 | P0 | — | Unpublished status recorded; Console package, signing/enrolment, prior upload/version codes and policy status verified without secrets; no legacy user-data migration required |
| SAS-003 | Collect pilot corpus and validate choir workflows | android/product | M0 | P0 | — | Representative PDFs/audio/arrangements available privately; sharing and device needs recorded; sample rights established |
| SAS-004 | Select backend, website hosting and hosted-MVP scope | backend/infrastructure | M0 | P0 | SAS-003 | Decision compares Firebase/AWS with costs, regions, identity and operations; maintainer choice recorded; dependent milestone assignments updated |
| SAS-005 | Establish data inventory and legal/content requirements | legal | M0 | P0 | SAS-003 | Data/SDK inventory, audience, content permissions and policy deliverables identified; no claim of legal clearance |
| SAS-006 | Single-song reader/audio prototype on the modern foundation | android | M0 | P0 | SAS-022 | [Technical prototype specification](sas-006-reader-audio.md): generated fixtures import/read/play offline; restart and failure cases pass; renderer/device evidence recorded. Real choir validation remains SAS-003 |
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
| SAS-017 | Internal/closed choir pilot and release candidate | android/release | M2 | P0 | SAS-002, SAS-007, SAS-010, SAS-015 | Clean install and pilot-to-pilot update tested; two rehearsals and offline programme run; failures resolved; final store screenshots captured |
| SAS-018 | Store submission and controlled production rollout | release | M2 | P0 | SAS-016, SAS-017 | Review/app access complete; release evidence, monitoring and forward-fix plan ready; approved release rolled out under agreed controls |
| SAS-019 | Personal annotations and performance improvements | android | M3 | P1 | SAS-017 | Pilot-prioritized subset scoped; marks tied to score revision; replacement preserves previous annotations |
| SAS-020 | Hosted operations and launch verification | infrastructure/backend | M4 | P0 | SAS-013, SAS-014 | Restore, outage, tenant isolation, deletion, cost and compatibility checks pass; production runbook recorded |
| SAS-021 | Structured notation feasibility and instrument plan | android/backend | M5 | P2 | SAS-017 | Real scores evaluated for fidelity/playback/licensing; supported subset and go/no-go decision documented |
| SAS-022 | Modernize Android framework, toolchain and dependencies | android | M0 | P0 | SAS-001 | [Task specification](sas-022-android-modernization.md): stable compatible toolchain pinned; Compose/AndroidX foundation builds and launches; obsolete plugins/repositories removed; checks and mise tasks updated |

If SAS-004 selects hosted MVP, SAS-012/013/014/020 move to M2; SAS-016 and SAS-017 additionally depend on SAS-014, and SAS-018 additionally depends on SAS-020. Adjust pack scope if live sharing replaces it, while preserving export/backup requirements. Parent rows must be split into bounded tasks before implementation when they cover multiple independently reviewable outcomes.

Initial project-management scaffolding is recorded in the [log](../log.md); it does not complete any product/backend/release task above.

## Current delivery state

**SAS-022 is Done (2026-10-31).** The minimal Android foundation builds, passes lint and launches on API 26 and API 36 emulators, including rotation/window checks. See [implementation and validation evidence](sas-022-modernization.md). SAS-002 remains the unpublished Console audit, SAS-003 prepares pilot content/device needs, and SAS-006 is Done for the authorized generated-fixture technical prototype; [evidence](sas-006-prototype.md) covers offline private imports, actual PDF/audio, restart, rollback and display checks. SAS-003 remains the real-content/pilot gate. SAS-002/003 remain Backlog; SAS-022 does not complete the real PDF/audio prototype or a release.


## Capability and review guidance

These are initial workload assessments, revisited when parent tasks are split. Model suggestions and assignments follow the [workflow](workflow.md); no model/provider is required for unassigned work.

| Task | Capability | Review needs | Escalation trigger |
| --- | --- | --- | --- |
| SAS-001 | Standard | Preserved history/source, reconciled docs and tracker consistency | Ambiguous history reconciliation or destructive migration appears necessary |
| SAS-002 | Standard | Unpublished Console identity, signing and upload configuration | Console/package ownership or signing access is unclear |
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
| SAS-022 | Standard | Toolchain compatibility, dependency inventory, clean build and launch evidence | Dependency/renderer compatibility requires a significant architecture choice |

## Current assignments

- **SAS-001:** assigned to OpenAI GPT-6.1 Sol, medium reasoning, in Orca tab `SAS-001 · Sol`.
- **Session:** `term_0092226f-88ca-4e6d-a6f5-d911f1beee59`.
- **Workspace:** `fa47b814-1c6f-473c-bfbc-691891b009cf::/home/henri/IdeaProjects/sing-a-song/`.
- **Handoff evidence:** Orca accepted prompt request `74e97311-822f-43f6-af17-8c9d4aebd9a5` on 2026-10-30. Local execution is verified in the [integration report](sas-001-integration.md); the maintainer removed GitHub tracker setup from the required scope.
- **SAS-001 status: Done (2026-10-30).** History/source integration, planning reconciliation and stable local tracking are verified in the [integration report](sas-001-integration.md). Maintainer direction on 2026-10-30 removed GitHub tracker management and remote issue links from acceptance; the local backlog remains authoritative. SAS-001 closed without an APK requirement; its historical SDK/license/build follow-up is now resolved by SAS-022.
- **SAS-022:** Done (2026-10-31); assigned to OpenAI GPT-6.1 Sol, high reasoning, Orca tab `SAS-022 · Sol`, session `term_8a2a5436-6493-4a8f-9723-bb92bc1b97db`, in the same workspace as SAS-001. Implementation, clean build/lint, API 26/36 launch/recreation tests and API 36 display checks passed. Source/dependency review, documentation checks and Git whitespace checks passed; see the [implementation evidence](sas-022-modernization.md).
- **SAS-022 execution authorization:** maintainer requested uninterrupted execution and necessary permissions. Session uses unrestricted filesystem/network execution with approval prompts disabled. Scope includes local modernization, toolchain/SDK installation and build/device checks; no publishing, paid infrastructure, secret changes or destructive Git actions.
- **SAS-006:** Done (2026-10-31); OpenAI GPT-6.1 Sol, high reasoning, continuing in the existing checkout/session. Maintainer authorized generated fixtures and separating technical prototype completion from SAS-003 pilot validation. Build/lint/fixtures, nine tests on each API 26/36, real-picker/offline/restart and display checks passed; see the [prototype evidence](sas-006-prototype.md). Source/dependency review, documentation and Git whitespace checks passed.
- Other unfinished tasks remain Backlog and unassigned. Keep status and assignment updates in this backlog; no GitHub authority transfer is scheduled.
