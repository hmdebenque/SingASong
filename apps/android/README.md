# Android application

Status: reboot planned; Kotlin is confirmed, no reboot scaffold exists. Legacy source is preserved separately in `app/`, using the original root Gradle build/wrapper. Its build/runtime and upgrade compatibility are unaudited; do not move or replace it as part of planning integration.

Owns the native phone/tablet app, Gradle wrapper/build, local metadata and migrations, reader, recordings, programme packs and Android release configuration. Existing Play identity and signing must be audited before choosing the new application ID or upgrade strategy.

Entry tasks: SAS-002/006/008/009/010 in the [backlog](../../docs/project/backlog.md). Proposed UI/persistence/media choices are in the [roadmap](../../ROADMAP.md). Add prerequisites, commands and actual implemented status when bootstrapping. Keep keystores and real choir files out of source control.
