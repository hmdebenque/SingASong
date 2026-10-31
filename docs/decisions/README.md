# Project decisions

An accepted decision is not evidence of implementation. Only explicit user/maintainer agreement establishes acceptance; routine execution within the agreed scope does not require a new decision record.

| Record | Status | Scope |
| --- | --- | --- |
| [0001: Product constraints](0001-product-constraints.md) | Accepted | Explicit user requirements and existing resources |
| [0002: Repository organization](0002-repository-organization.md) | Proposed overall; Android directory `app/` explicitly confirmed | Component ownership and management conventions |
| [0003: Backend and hosting](0003-backend-and-hosting.md) | Proposed | Firebase versus dedicated AWS service |
| [0004: Android foundation](0004-android-foundation.md) | Accepted | Maintainer-authorized Kotlin/Compose modernization and provisional device baseline |
| [0005: Reader/audio prototype](0005-reader-audio-prototype.md) | Accepted for bounded prototype | Generated-fixture validation, isolated platform PDF rendering and foreground Media3 playback |

Use the next unused four-digit number across all categories and the [template](template.md). Categories may include product, android, website, backend, infrastructure, legal, design and cross-cutting. Statuses are Proposed, Accepted, Rejected or Superseded. Link replacement decisions in both directions and retain historical reasoning. Record the maintainer agreement/date when accepting a proposal.

Website framework, backend language, identity provider, database, region, IaC tool, visual identity, monetization and exact hosted-MVP boundary remain open. Do not inherit these choices from dsr-engine.
