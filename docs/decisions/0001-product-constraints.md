# 0001: Product constraints and existing resources

- Status: Accepted
- Date: 2026-10-30
- Categories: product, android, cross-cutting
- Agreement/evidence: Maintainer requests in this project conversation on 2026-10-30.

## Confirmed requirements

Reboot SingASong as an Android Kotlin app. Prioritize choir songs for the MVP: finding and reading music, practising tutti or voice parts, preparing shared event programmes, and supporting different representations of a song. Broader instruments are later work. Include website and backend ownership in this multitechnology repository and include Play Store, legal, design and infrastructure delivery in planning.

The maintainer already has a Play developer account/app and an AWS account, and is experienced in Java, Go and Rust backend development. Firebase and dedicated backend options must be evaluated.

The maintainer subsequently confirmed that the app was never published and has no users. No legacy installed-user data migration or production-upgrade compatibility is required. Preserve source history as reference and modernize the Android framework/libraries to a compatible stable 2026 foundation. Existing Play Console identity/signing configuration still needs verification before publishing.

The maintainer selected a flagship Pixel from roughly four years ago as the initial reference phone. The working emulator target is Pixel 7 Pro (2022), using its official hardware profile and the existing Android 16/API 36 image. This establishes an emulator target, not physical-device validation or a new minimum Android version. See the [emulator guide](../guides/pixel-emulator.md).

## Repository ownership update — 2026-10-31

The maintainer owns [hmdebenque/SingASong](https://github.com/hmdebenque/SingASong) and authorizes resets/restructuring when useful for the reboot. Historical repository-preservation/pull-only assumptions no longer constrain design. See [ownership evidence](../project/repository-ownership.md); no reset/push is implied, and current prototype files/uncommitted work must remain recoverable. Local backlog authority and product/data-integrity requirements remain intact.

## Boundaries

The detailed roadmap, portable-pack-first approach, cloud provider, backend language, website framework and technology versions outside the authorized [Android foundation](0004-android-foundation.md) remain recommendations, not confirmed choices. No Play Console audit, signing verification or deployment has occurred here.

## Implementation status

SAS-022 implements the minimal modern Android foundation; original legacy source remains in Git history. Build/device evidence and limits are recorded in the [modernization report](../project/sas-022-modernization.md). SAS-006 adds a generated-fixture offline PDF/recording workspace; see its [evidence](../project/sas-006-prototype.md). SAS-008 adds the [first local library slice](../project/sas-008-library-evidence.md); lyrics, preferred-part/filtering, backup/restore and programmes remain open. Website, backend and infrastructure remain unimplemented. See the [roadmap](../../ROADMAP.md) and [backlog](../project/backlog.md).
