# 0001: Product constraints and existing resources

- Status: Accepted
- Date: 2026-10-30
- Categories: product, android, cross-cutting
- Agreement/evidence: Maintainer requests in this project conversation on 2026-10-30.

## Confirmed requirements

Reboot SingASong as an Android Kotlin app. Prioritize choir songs for the MVP: finding and reading music, practising tutti or voice parts, preparing shared event programmes, and supporting different representations of a song. Broader instruments are later work. Include website and backend ownership in this multitechnology repository and include Play Store, legal, design and infrastructure delivery in planning.

The maintainer already has a Play developer account/app and an AWS account, and is experienced in Java, Go and Rust backend development. Firebase and dedicated backend options must be evaluated.

The maintainer subsequently confirmed that the app was never published and has no users. No legacy installed-user data migration or production-upgrade compatibility is required. Preserve source history as reference and modernize the Android framework/libraries to a compatible stable 2026 foundation. Existing Play Console identity/signing configuration still needs verification before publishing.

## Boundaries

The detailed roadmap, portable-pack-first approach, cloud provider, backend language, website framework and technology versions outside the authorized [Android foundation](0004-android-foundation.md) remain recommendations, not confirmed choices. No Play Console audit, signing verification or deployment has occurred here.

## Implementation status

SAS-022 implements the minimal modern Android foundation; original legacy source remains in Git history. Build/device evidence and limits are recorded in the [modernization report](../project/sas-022-modernization.md). SAS-006 adds a generated-fixture offline PDF/recording workspace; see its [evidence](../project/sas-006-prototype.md). Full choir catalogue/programmes, website, backend and infrastructure remain unimplemented. See the [roadmap](../../ROADMAP.md) and [backlog](../project/backlog.md).
