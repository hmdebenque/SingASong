# Development and validation

The implemented Android foundation uses Kotlin, AndroidX, Compose and Material 3 under `app/`. All Gradle configuration and the wrapper stay inside that component. Root mise tasks supply one pinned Java 17 runtime and the Android SDK, and expose real build/lint/device checks. See the [Android README](../../app/README.md) for prerequisites and emulator commands and the [SAS-022 report](../project/sas-022-modernization.md) for verified versions/evidence. The [Android architecture guide](../architecture/android.md) records current UI structure and the maintainer-directed VenezDiner reference for later feature/design tasks.

Run from the repository root:

```sh
mise run build:app
mise run lint:app
mise run check:app
mise run check:fixtures
mise run test:app
python3 scripts/check_docs.py
git diff --check
```

`check:app` verifies generated fixture bytes/hashes, builds and lints the debug app. `test:app` requires an available authorized Android device/emulator and exercises real private imports, PDF rendering, WAV/MP3 decoding, rollback, persistence, playback/seek and lifecycle behavior. See [SAS-006 evidence](../project/sas-006-prototype.md). There is no domain unit-test suite yet. SDK setup is explicit through `setup:app` (also a prerequisite of app tasks); it verifies package installation and requires reviewed license acceptance when first needed. No task automatically accepts legal terms. Gradle does not auto-download SDK packages. SDK license acceptance/install and emulator launch were authorized and exercised during SAS-022; other contributors perform their own setup.

The direct Python check needs only Python 3. It verifies local Markdown inline-link file targets, trailing whitespace and final newlines in Markdown files. It skips external URLs, headings and code blocks; it does not validate remote links, Markdown anchors, legal correctness or application behavior.

No website framework, backend compiler or cloud tool is selected. SAS-007 still owns CI and delivery automation after its dependencies pass; SAS-022 supplies the real Android checks it can reuse. Do not copy reference-project pins or install unrelated toolchains. Keep component checks truthful and document unavailable verification.

When other components become runnable, document exact prerequisites/commands in their READMEs and connect relevant checks to CI. Plan separate Android, website, backend and infrastructure jobs plus contract/consumer checks for shared-interface changes. Run relevant jobs for cross-component work. Publishing/deployment jobs remain separate from pull-request validation and use protected credentials/environments.

Android feature validation includes persistence/import failure cases, private media and real offline reader/audio/device behavior where implemented. Website validation includes build, links and accessibility; backend validation includes authorization, persistence and API compatibility; infrastructure validation includes syntax/plan and reviewed deployment/restore evidence. Future checks must not report success for missing implementations.
