# Development and validation

The reboot provides documentation and a Python documentation check. The preserved legacy Android application, Gradle configuration and wrapper are all in `app/`; `mise run build:app` invokes its debug APK build. The build task was attempted but Gradle 4.4 rejected the installed Java 21 runtime before project evaluation; no APK was produced. SAS-002/006 must audit compatibility before selecting a new build foundation. A root `mise.toml` supplies `build:app` and `check:docs` tasks. It does not install or pin a replacement toolchain; the [Android README](../../app/README.md) lists legacy JDK/SDK prerequisites. No Node manager, backend compiler or cloud tool is selected.

Run from the repository root:

```sh
python3 scripts/check_docs.py
```

The check verifies local Markdown inline-link file targets, trailing whitespace and final newlines in Markdown files. It skips external URLs, headings and code blocks; it does not validate remote links, Markdown anchors, legal correctness or application behaviour. Use `git diff --check` once the workspace is a Git checkout.

The maintainer selected mise task entry points. During SAS-007, extend the root task registry with real component build/lint/test tasks and an aggregate check. Select actual supported tool versions with the component bootstrap; do not copy dsr-engine's Rust/Node pins. Keep the Android Gradle wrapper and commit applicable application lockfiles. Check tasks must not implicitly provision cloud resources or install unrelated toolchains.

When runnable, each component must document prerequisites and exact commands in its README and connect relevant checks to CI. Plan separate Android, website, backend and infrastructure jobs plus contract/consumer checks for shared-interface changes. Run all relevant component jobs for cross-cutting changes. Publishing/deployment jobs are separate from pull-request validation and use protected credentials/environments.

Android validation includes unit/device tests where appropriate; website validation includes build, links and accessibility; backend validation includes authorization, persistence and API compatibility; infrastructure validation includes syntax/plan and reviewed deployment/restore evidence. Do not label those checks as available until implemented.
