# SingASong reboot roadmap

Project execution is tracked in the [backlog](docs/project/backlog.md) using the [delivery workflow](docs/project/workflow.md). See the [documentation index](docs/index.md) for component ownership and decision records. This file remains the canonical detailed roadmap; task completion is tracked separately.

Proposal — 30 October 2026. Scope: native Android app written in Kotlin, starting with choir repertoire. Phases describe delivery order, not calendar commitments. Android product work, backend/infrastructure, and Play Store/legal/design readiness are coordinated delivery tracks.

## Product direction

Help a choir singer find the right music, practise their voice part, and follow an entire rehearsal or concert reliably, including without internet access.

The primary workflow is: receive a concert pack → choose my voice part → open a song → read while listening → practise a passage → move to the next song.

The secondary workflow is for a repertoire organiser: import songs and files → identify arrangements and voice parts → assemble an ordered programme → share it with singers.

Success means less time finding files and more time singing. General instrument support remains a later expansion.

## Starting point

The initial planning workspace was empty. SAS-001 has since imported the original repository and all 29 commits through `26e83a75533d160fa636d61895cd48d76696b1d8` (October 2019), preserving older Java code, a Kotlin activity, song/verse models, import/export code and parallel package trees in their original locations. Legacy source remains in `app/`; the proposed reboot layout is unimplemented. See the [integration report](docs/project/sas-001-integration.md). This is a structural inspection, not a build or runtime audit.

Recommendation: create a modern Kotlin application foundation while preserving repository history and any useful data contracts. Before replacing code, inspect actual legacy exports and determine whether anyone needs installed-app data migrated. Do not assume the legacy app already supports the proposed reader or playback features.

Source: [original repository](https://github.com/frenchfrie/SingASong).

### Existing infrastructure and expertise

- A Google Play developer account and an app already exist. Reuse the existing listing and update path where possible; registration and creating a new app are not baseline tasks.
- An AWS account is available, and the maintainer is experienced in backend development with Java, Go and Rust. A dedicated API is therefore a realistic option alongside Firebase.
- Play Console configuration, signing access, existing users, cloud resources and billing have not been inspected. Phase 0 audits these rather than assuming readiness.

## Proposed MVP boundary

Assumptions to validate with a pilot choir:

- Source material is mainly PDF scores, plain lyrics, and existing rehearsal recordings.
- One organiser prepares each shared programme; singers keep personal reading preferences.
- The first pilot uses Android phones/tablets. Other members can receive a conventional programme and source files.
- Manual sharing and importing of concert packs is acceptable initially. If automatic choir-wide updates are essential, move the shared-workspace phase into MVP and re-estimate.
- Proposed first release: local use and portable packs without mandatory accounts. Backend architecture, hosting for public legal/support pages, release automation and operating costs are planned from Phase 0. Shared accounts can enter the MVP if the distribution decision requires them.

### Include

| Capability | MVP behaviour |
| --- | --- |
| Library | Create/edit songs, import files through Android's file picker/share flow, list and search by title, composer/arranger, tags and entered lyrics; filter by voice-part availability. Case/accent-insensitive search. |
| Song organisation | One song can contain several named arrangements. Each arrangement groups its PDFs, lyrics and recordings. Show full score and individual-part PDFs as selectable variants. |
| Voice preference | Remember the singer's preferred part: soprano, alto, tenor, bass (SATB), with custom labels such as S1/S2 or soloist. Clearly indicate unavailable parts; allow manual selection. |
| Reader | Offline PDF viewing, zoom, fit width/page, large page-turn targets, continuous or paged reading, keep-screen-awake, remembered position, portrait/landscape and tablet layouts. |
| Lyrics | A separate adjustable-text view, manually entered or imported. No promise of extracting correct lyrics from PDFs. |
| Playback | Attach supported recordings, label them tutti/voice part/accompaniment, select one at a time, play while reading, seek, repeat a selected A–B passage, and adjust speed without intentional pitch change. Include background playback and headset/lock-screen controls. |
| Programmes | Create an ordered rehearsal/concert set list with name, optional date, notes, and a specific arrangement per entry. Navigate between songs from the reader. |
| Sharing | Export/import a versioned concert pack containing programme metadata and selected local scores, lyrics and recordings. Also export a readable programme for people without the app. |
| Reliability | Explicit offline-readiness status, missing-file warnings, persisted imports, backup/export, and actionable errors for unsupported or corrupt files. |

### Defer

- MusicXML/MIDI import, synthesized score playback, note highlighting and automatic page following.
- Simultaneous mixing of voice recordings, source separation and automatic alignment.
- Full pen/stylus annotation tools and shared annotation layers.
- Automatic PDF music recognition, lyrics extraction and notation editing.
- Accounts, live cloud collaboration, chat, attendance, payments and calendar integration.
- Guitar chord editing/transposition, tablature and instrument-specific tools.
- iOS/web clients; a web repertoire editor can be reconsidered when organiser workflows justify it.

### Playback distinctions

1. **Attached recordings — MVP:** selecting a tenor recording or a tutti recording. A PDF remains a visual document and does not supply notes to play. Switching recordings need not preserve musical position because files may use different timings.
2. **Aligned recording mixer — later:** simultaneous stems with per-part volume, solo and mute. Requires compatible timelines and a playback engine validated for synchronization; separate arbitrary rehearsal takes are insufficient.
3. **Notation playback — later:** import structured notation, interpret repeats/tempo/parts, synthesize sound and map playback to the displayed score. MusicXML is a candidate interchange format, not a complete rendering/playback engine. PDF recognition is a separate problem.

## Roadmap and release gates

### Phase 0 — Validate content and build the smallest working slice

- Collect a representative pilot corpus: roughly 10–20 songs with permission to use them, scanned and digital PDFs, long scores, lyrics, tutti and individual-part recordings, and multiple arrangements of one song.
- Observe a singer and an organiser preparing and using a real rehearsal programme.
- Inventory legacy data and choose migration requirements and supported Android devices. Audit the existing Play app identity, signing keys/enrolment, version codes, policy status and installed-user upgrade path.
- Complete the backend decision record and infrastructure baseline described below; choose local-pack versus hosted-sharing MVP scope.
- Inventory data/SDKs, draft the privacy and content-rights approach, and define the visual identity and store asset brief.
- Sketch Library → Song → Reader/player and Programmes → Programme → Reader flows.
- Prototype one imported PDF with one recording on a phone and tablet. Validate readable layouts, page navigation, playback interruptions and offline reopening.
- Assess PDF renderer choices against real documents, memory use, supported Android versions and licensing. Pin dependencies only after this check.

**Exit gate:** an imported song can be read and heard offline on representative devices; content formats, sharing expectations and migration needs are recorded. Existing Play signing/update feasibility is established, the backend decision is documented, and release/legal/design work has owners and deliverables.

### Phase 1 — Personal choir library and dependable reader

- Build the Kotlin foundation, persistent song/arrangement/resource model, file import and search.
- Implement PDF and plain-lyrics reading, voice-part selection, position restoration and local backup.
- Keep imported resources in app-managed storage so moving the source file does not silently break a song.
- Handle duplicate imports, unsupported documents and interrupted imports explicitly.

**Exit gate:** the pilot corpus is searchable and readable in airplane mode after app restart; two arrangements of the same song remain distinguishable; phone rotation preserves reading state.

### Phase 2 — Rehearsal playback

- Attach and label recordings; default to the preferred voice part when available.
- Add reader-integrated controls, background playback, speed control and A–B repeat.
- Handle audio focus, calls/headphone disconnection and lifecycle changes.
- Keep reading and audio positions independent unless a later explicit synchronization feature links them.

**Exit gate:** a singer can practise a difficult passage while reading, change speed, repeat it, and resume safely after interruption without losing their place.

### Phase 3 — Concert packs and choir pilot: MVP release

- Create/reorder programmes and navigate their songs from the reader.
- Export/import complete portable packs and a human-readable programme.
- Use stable IDs, a pack schema version and content revisions. Show an import summary and handle re-imports without duplicating unchanged content.
- Offer explicit replacement or keeping a separate copy for conflicting revisions. Preserve personal preferences and reading state; never silently overwrite a different arrangement.
- Validate archive paths, file sizes and references; stage imports so a failure leaves the existing library usable.
- Show pack size before sharing and support saving through Android's document picker for transfer methods with larger limits.
- Run at least two rehearsals with a small group, then a full programme rehearsal in airplane mode.
- Complete the Play Store release gate below: signed update, tested migration, store assets, legal pages/declarations, internal/closed testing and staged production rollout. If hosted sharing is included, also pass the backend launch gate.

**MVP acceptance:** another singer can import the organiser's pack, select their part, read and listen to every included song offline, and traverse the programme without returning to the library. Re-importing the same pack creates no duplicates. Missing materials are visible before rehearsal. Backup restores successfully on a fresh installation. The Play release gate passes before public launch; a functional prototype alone does not constitute the MVP release.

### Phase 4 — Better practice and performance

Prioritise from pilot feedback:

- Personal PDF highlights, breath marks and notes, bound to an exact score revision.
- Named passage bookmarks, manual page/audio cues, starting-note reference and count-in.
- Performance mode with minimal controls, orientation lock, optional pedal page turning and a pre-performance readiness check.
- Personal practice queues and optional self-assessed learning status.
- Recording directly into a song, followed by aligned-stem mixing only if the source material supports it.

**Exit gate:** singers can prepare and reuse their own rehearsal markings without losing them when shared materials change. Score replacement must preserve the old annotated revision or require explicit remapping.

### Phase 5 — Shared choir workspace

- Add choir membership/invitations and organiser/editor/singer roles.
- Publish repertoire and programme revisions, download selected programmes for offline use, and display update status.
- Keep private notes separate from choir-owned content; define conflict and deletion behaviour before implementing synchronization.
- Provide a published programme revision that can remain fixed during a performance.
- Consider a small web import/editor workflow if preparing repertoire on a phone is a major obstacle.

**Exit gate:** an organiser publishes a revision, singers see what changed, and previously downloaded programmes remain usable without a connection.

This phase activates the shared-workspace backend selected in Phase 0. Provisioning, authentication, storage, authorization, synchronization, deletion and operations follow the backend track below. If shared updates are required for the MVP, move this scope ahead of the Phase 3 release gate and re-estimate; do not hide it inside file sharing.

### Phase 6 — Structured music and broader instruments

- First run a bounded MusicXML feasibility study: real choral scores, repeats, divisi, lyric alignment, rendering fidelity, engine licensing and Android integration.
- Then deliver notation playback, voice solo/mute/volume, tempo changes and score-following for an explicitly documented supported subset.
- Add guitar chord/lyrics views, transposition and piano/instrument parts as additional arrangement resources.
- Treat PDF-to-notation recognition as a separate optional research project.

**Exit gate:** representative supported scores render and play correctly, and unsupported notation produces clear limitations rather than misleading playback.

Phases 4 and 5 can be reordered based on whether individual practice or choir distribution is the larger problem. Phase 6 should not block the choir MVP.

## Infrastructure and backend delivery track

### B0 — Architecture decision, alongside Phase 0

Record an architecture decision comparing the following options with the same scenario: one organiser publishes a programme and files, invited members download it, membership is revoked, a new revision is published, and a user requests deletion.

| Option | Proposed components | Strengths for this project | Work and trade-offs |
| --- | --- | --- | --- |
| Firebase / Google managed services | Firebase Authentication, Firestore metadata, Cloud Storage media, Functions or Cloud Run for privileged operations; Hosting for public pages; optional FCM | Fast mobile integration; managed authentication and database tooling; less initial service plumbing | Security Rules and privileged-server authorization, document modelling, revision/conflict semantics, file downloads and cost controls still need implementation; provider coupling |
| Dedicated API on AWS | Cognito or another managed OIDC identity provider; a Java, Go or Rust API; private S3 media; PostgreSQL or DynamoDB metadata; CloudWatch; static HTTPS hosting | Fits existing expertise; explicit domain/API model; flexibility for future web clients and music processing | Own API deployment, migrations, authorization, synchronization, backup/restore and on-call work; database/network baseline costs depend on topology |

**Working recommendation:** lean toward a small dedicated AWS backend given the maintainer's expertise, but choose after a bounded spike and cost estimate. Firebase remains the alternative if reducing platform work is the highest priority. Google Play distribution does not require Firebase, and Google sign-in does not require storing repertoire in Firebase.

For AWS, choose one language and one deployment model. Go with API Gateway/Lambda and DynamoDB is a candidate for intermittent pilot usage; a Java/Go service on ECS Fargate with PostgreSQL is a candidate when relational modelling and conventional service operations are preferable. Rust is viable if its maintenance/tooling trade-off is preferred. These are alternatives, not components to deploy together. Do not introduce Kubernetes or microservices for the MVP.

A managed authentication provider is preferable to building password handling. Firebase Crashlytics or FCM can be used with an AWS backend if justified; each additional SDK adds data-disclosure and operational work.

Sources: [AWS serverless reference architecture](https://aws.amazon.com/lambda/resources/refarch/refarch-webapp/), [Firestore security model](https://firebase.google.com/docs/firestore/security/overview). Firebase mobile-client rules and privileged server/IAM access have different security boundaries; test both.

**B0 deliverables:** decision record, system/data-flow diagram, chosen region and identity provider, cost model, API/sync outline, and explicit hosted-MVP scope. No production cloud spend is necessary merely to make this decision.

### B1 — Environment and deployment foundation, alongside Phases 1–2

- Establish development/staging/production separation, infrastructure as code, reproducible deployments and narrowly scoped CI credentials. Prefer workload identity/OIDC over persistent cloud keys where supported.
- Secure cloud/root accounts, billing contacts and recovery access; configure secrets management and role-based access.
- Provide a stable domain, HTTPS, support mailbox/contact and public privacy/support pages. Add the public account-deletion route before an account-enabled launch.
- Build Android CI for checks and signed release artifacts; protect signing material, upload credentials and production publishing access. Keep deployment and public release as distinct steps.
- For hosted scope, deploy private media storage, metadata persistence and authenticated API/rules. Establish logs with retention, errors/latency dashboards, alerts, backups and a restore procedure.
- Record dependency and native-library licences, versions and update ownership; include PDF/audio engines in this inventory.

**B1 gate:** staging is reproducible, a release artifact reaches internal Play testing, public support/legal URLs work, and secrets are absent from source and application packages. For a local-only MVP, provision public pages and CI but defer unused account/API/database services.

### B2 — Shared repertoire backend, before the first hosted release

- Implement users, choirs, memberships, invitations, role checks and tenant isolation. Authentication alone must not grant access to every choir.
- Implement song/arrangement/resource metadata and ordered programme revisions with stable IDs, validation and idempotent writes.
- Upload large files directly to private object storage through authorized, limited-duration upload/download access. Validate completion, size, type and checksum; clean up abandoned uploads.
- Publish immutable resource revisions and programme manifests. Clients download to local storage, verify completeness and retain the last usable version after an interrupted update.
- Define update cursors/version checks, retries, tombstones and edit-conflict handling. Start with organiser-controlled publication rather than concurrent editing by all singers.
- Membership revocation prevents future server access; it cannot reliably recall files already exported or copied offline. Define and communicate this boundary.
- Implement export, account deletion, choir ownership transfer/deletion and retention expiry. Deleting an authentication record alone does not remove application data or media.
- Test cross-choir denial, expired invitations/URLs, revoked users, duplicate requests, partial uploads and interrupted synchronization.

**B2 gate:** two test choirs cannot access each other's data; publication/download works end to end; existing downloads survive an outage; deletion and backup restoration have been exercised. Pass this before enabling hosted sharing for real users.

### B3 — Costs and ongoing operations

Model pilot and growth scenarios (for example 1, 10 and 100 choirs) using measured file sizes and expected download frequency. Track stored GB including old revisions/backups, monthly download GB, database operations, compute, authentication, email, logs, and any fixed database/network charges.

For illustration, a 500 MB programme downloaded by 40 members transfers roughly 20 GB before retries or updates. Media transfer can dominate a small metadata API, so use checksums and revision-aware downloads rather than downloading everything again.

Firebase Cloud Storage requires a Blaze billing plan under the current rollout; budget alerts are not a spending cap. Check the current [storage billing requirements](https://firebase.google.com/docs/storage/faqs-storage-changes-announced-sept-2024) and [Firebase prices](https://firebase.google.com/pricing) when estimating. Price the selected AWS region/topology before committing; do not assume the existing account makes hosting free.

Set upload/storage quotas, request limits, budget alerts and a response plan for unexpected usage. Define backup retention, acceptable data loss and restore time, then test them. Keep a short runbook for outages, leaked credentials, failed migrations and rollback/forward fixes. Review costs, dependency updates, store policies and support requests regularly after release.

## Play Store, legal and design delivery track

### P0 — Audit the existing app, during Phase 0

- Confirm the existing application ID/package, listing URL, account owner/contact details, verification status, country availability, active tracks, installed audience and any policy warnings.
- Inspect Play App Signing enrolment, signing certificate and upload-key access. Preserve the update identity, choose an increasing version code, and document recovery. An upload-key reset and a signing-key change are different operations.
- Inspect the currently distributed build and storage schema. Decide whether users receive an in-place migration or an explicit supported export/import path, then test an actual update from that build.
- Verify current target API, native-library/page-size compatibility, required declarations and any testing/production-access restrictions shown for this account. Do not assume new-personal-account requirements apply to an existing account.

Sources: [Android signing guidance](https://developer.android.com/studio/publish/app-signing), [Play target API requirements](https://support.google.com/googleplay/android-developer/answer/11926878). Recheck time-sensitive requirements at submission rather than freezing a target API deadline in this roadmap.

### P1 — Privacy, legal and content preparation, during Phases 0–2

- Document the publisher identity, support contact, personal data collected, purposes/legal bases, recipients/processors, retention, deletion/export processes and applicable international transfers. Choose hosting regions deliberately; EU hosting alone does not settle every GDPR obligation.
- Inventory SDK behaviour, including diagnostics, authentication and analytics. Decide whether analytics are needed and configure disclosures/consent where required; OS permission approval is not a substitute for all privacy obligations.
- Publish an accessible privacy policy, link it in the app and store, and complete Data safety accurately for the shipped build and its SDKs. A local-only app still needs the applicable store disclosures and privacy policy.
- For account creation, provide deletion initiation in the app and a working external web route, including application-data cleanup and clearly explained retention exceptions.
- Define terms of use and content-sharing rules before hosted uploads: users' rights to distribute scores/recordings, private choir access, reporting/takedown contact and handling abuse. Assess Google Play's user-generated-content requirements for the actual sharing design; implement reporting/moderation/blocking where applicable before launch.
- Track licences and attribution for code, fonts, icons, sample music and recordings. Use owned/licensed/public-domain demonstration content in screenshots and review accounts, checking arrangement/recording rights separately.
- Decide audience/age targeting, countries, free/paid positioning and whether minors are expected. Evaluate additional obligations if relevant. Monetization is deferred by default; subscriptions/digital sales require a separate billing-policy and commercial workstream before introduction.

This is a delivery checklist, not a claim of legal clearance. Review the final data flows and publication terms against the intended audience and jurisdictions.

Sources: [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311), [Data safety guidance](https://support.google.com/googleplay/android-developer/answer/10787469), [account deletion requirements](https://support.google.com/googleplay/android-developer/answer/13327111), [CNIL mobile application recommendations](https://www.cnil.fr/fr/recommandations-applications-mobiles).

### P2 — Product identity and listing assets, during Phases 1–3

- Define the visual identity: app name, adaptive launcher icon, colours, typography and accessible controls; reuse recognisable existing branding where useful.
- Prepare a separate Play icon, feature graphic, and accurate phone/tablet screenshots showing library, reader, voice-part playback and programme/offline readiness.
- Write French and English store titles/descriptions, release notes and concise onboarding/help. Describe only shipped capabilities; avoid implying PDF synthesis or cloud sync in a recording/pack-based release.
- Use readable, permission-cleared sample repertoire. Capture screenshots from the release candidate and check device/layout coverage and current asset requirements.
- Keep editable artwork and copy in version control or a documented asset location. Public support/privacy pages can be simple static pages; a marketing website is optional.

Source: [Google Play preview asset requirements](https://support.google.com/googleplay/android-developer/answer/9866151).

### P3 — Testing, review and production gate, at Phase 3

- Produce the signed release artifact appropriate for the existing app, preferably an Android App Bundle with the compatible signing setup; archive mapping/native-symbol files where relevant.
- Use internal testing for developer verification and closed testing for the pilot choir; follow any account-specific testing requirement shown in Console. Review pre-launch reports and accessibility/device failures.
- Test install/update, existing-data migration, offline use, background audio, supported Android versions, tablet layouts and recovery after interrupted imports/downloads.
- Complete Console app-content declarations: privacy/Data safety, ads status, audience, content rating, app access and permission/foreground-service declarations as applicable. Provide reviewer credentials and access instructions if functionality is gated.
- Check listing assets, support links and account deletion where applicable against the release candidate. Confirm cloud production configuration and backend launch gates if accounts/sharing are enabled.
- Allow time for store review and remediation. Use controlled publishing/staged rollout, monitor crashes/ANRs and support feedback, and define stop-rollout criteria.
- Prepare a forward-fix release and backwards-compatible server/database migrations. Halting a rollout does not downgrade devices that already installed it.

**Public-release gate:** tested signed update + approved store submission + truthful listing + accessible legal/support pages + completed applicable declarations + operational monitoring/support. Hosted releases additionally require authorization/deletion/restore checks and a reviewed cost baseline.

Source: [prepare an app for review](https://support.google.com/googleplay/android-developer/answer/9859455).

### Delivery dependencies and ownership

| Product milestone | Infrastructure/backend dependency | Publishing/design/legal dependency |
| --- | --- | --- |
| Phase 0 prototype | B0 decision and cost assumptions | P0 existing-app audit; P1 data inventory; visual brief |
| Phases 1–2 internal builds | B1 CI, staging as needed, public-page hosting | Signing/update proof; privacy drafts; icon and listing copy drafts |
| Phase 3 local-pack MVP | B1 complete; hosted services may remain deferred | P1–P3 complete; pilot feedback addressed; production release gate |
| First hosted choir release (Phase 5, or earlier if chosen) | B2 complete; B3 operating controls active | Revised privacy/Data safety; account deletion; content-sharing controls; reviewer access |
| Each later release | Compatibility, migrations, backups, cost review | Policy/SDK disclosure review; fresh screenshots if needed; rollout monitoring |

The maintainer owns backend/IaC/release engineering by default, with Android implementation and design/legal deliverables tracked explicitly. Outsourcing is optional, but these tasks need capacity in the estimate. Treat store review and pilot rehearsal dates as external dependencies; estimate product, cloud and publishing work separately after Phase 0.

## Data concepts to establish early

| Concept | Responsibility |
| --- | --- |
| Song | Musical work identity: title, creators, tags. |
| Arrangement | A particular setting/version: arranger, voicing, optional key/language. A SATB setting and a guitar setting can belong to the same song. |
| Resource | PDF, lyrics, recording or future structured score attached to an arrangement; includes variant label, part labels, file type, content hash and revision. |
| Part | Flexible named voice/instrument assignment; SATB presets without limiting the model to four parts. |
| Programme / entry | Ordered references to arrangements, preferred resources and event-specific notes; entries have their own IDs so repeats are possible. |
| Personal state | Preferred part/view, last page, audio position and future private annotations, kept separate from shared repertoire. |

Store identity and revision separately. Do not identify songs by filename or title alone. Multiple views are linked resources, not automatically generated equivalents; plain lyrics, chord lyrics and notation may need independent authoring.

## Suggested Android direction

- Kotlin and Jetpack Compose with phone/tablet layouts; a small app organised by library, reader, playback and programme features.
- ViewModels and a repository/data layer, with durable local state as the basis for offline use. Start simply rather than splitting into many modules prematurely. See [Android architecture guidance](https://developer.android.com/topic/architecture).
- Room is a proposed local metadata store; app-managed files hold PDFs/audio, and Android document/share flows handle import/export. Validate these choices during the foundation work.
- Media3/ExoPlayer for ordinary recording playback; it provides the basic player abstraction and supports local/streamed media. A synchronized stem mixer remains a separate technical investigation. See [Media3 documentation](https://developer.android.com/media/media3/exoplayer).
- Keep PDF rendering and future notation rendering behind separate interfaces. The reader should not assume that every resource has notes, measures or a musical timeline.
- Validate import/export round trips, revision handling and persistence with automated tests; exercise reading, audio interruptions and offline use on physical devices. Include large text, accessible controls and French/English-ready UI strings.

## Ideas from ChoirMate

ChoirMate groups scores and rehearsal recordings around repertoire, voice parts and practice lists. This supports using the song/arrangement as the centre of SingASong's experience. Its wider calendar, messaging and attendance features can remain outside this MVP. See [ChoirMate overview](https://choirs.choirmate.com/).

Its current reader offers annotations, flexible page layouts and navigation between songs. Its recording mixer requires files with matching start points and durations. These are useful later benchmarks for reader quality and synchronized practice. See [ChoirMate release notes](https://www.choirmate.com/whats-new).

ChoirMate's FAQ describes PDF scores and MP3/M4A recordings, with MIDI converted before upload. That is evidence for a useful recording-based choir workflow, not a requirement to match its format choices exactly. See [ChoirMate FAQ](https://www.choirmate.com/faq).

For future structured notation, see [MusicXML's format overview](https://www.musicxml.com/).

## Highest-value additional suggestions

1. **My part first:** open the preferred score variant and recording, with an obvious way to switch.
2. **Ready for rehearsal:** one clear status for whether the whole programme's selected materials are available offline.
3. **Know the arrangement:** make arranger, voicing and revision visible enough to prevent singers using different editions accidentally.
4. **Keep concert content stable:** avoid applying new score/programme revisions in the middle of a performance.
5. **Make leaving easy:** retain portable exports and backups as the app grows.

## Decisions to confirm before implementation

- Which sample scores/recordings represent the first choir, and are individual-part recordings already available?
- Is manual pack sharing acceptable for the first pilot, or must members receive updates automatically?
- Which phone/tablet models and Android versions need support? How many choir members use iOS?
- What is the existing Play listing/package, and are signing/upload credentials available? Are there legacy exports or installed-app data that must survive the reboot?
- Is AWS the preferred operational home, or is reducing service maintenance with Firebase more important? What monthly pilot budget and hosting-region requirements should constrain the choice?
- Is the first priority home practice, reading during rehearsal, or concert use? The proposed MVP supports all three, with rehearsal/practice driving the first prototype.

The next implementation increment should be one real choir song imported, read and played offline. Estimate calendar effort after this slice and the sharing decision, rather than assuming that cloud collaboration or notation playback are small additions.
