# Development and validation

The reboot provides documentation and a Python documentation check. Android source, Gradle configuration and wrapper live in `app/`. The root `mise.toml` pins Java 8 for the legacy Gradle build, Android command-line tools, and a task-specific Java 17 for SDK provisioning. `mise run build:app` installs missing tools, runs `setup:app` to install SDK platform 27 / Build Tools 27.0.3, then assembles the debug APK. First-time SDK setup prompts for license acceptance. See the [Android README](../../app/README.md) for exact commands and current validation. No Node manager, backend compiler or cloud tool is selected. SAS-002/006 still own the legacy compatibility audit and modern app foundation.

Run from the repository root:

```sh
python3 scripts/check_docs.py
```

The check verifies local Markdown inline-link file targets, trailing whitespace and final newlines in Markdown files. It skips external URLs, headings and code blocks; it does not validate remote links, Markdown anchors, legal correctness or application behaviour. Use `git diff --check` once the workspace is a Git checkout.

The maintainer selected mise task entry points. During SAS-007, extend the root task registry with real component build/lint/test tasks and an aggregate check. Select actual supported tool versions with the component bootstrap; do not copy dsr-engine's Rust/Node pins. Keep the Android Gradle wrapper and commit applicable application lockfiles. Check tasks must not implicitly provision cloud resources or install unrelated toolchains.

When runnable, each component must document prerequisites and exact commands in its README and connect relevant checks to CI. Plan separate Android, website, backend and infrastructure jobs plus contract/consumer checks for shared-interface changes. Run all relevant component jobs for cross-cutting changes. Publishing/deployment jobs are separate from pull-request validation and use protected credentials/environments.

Android validation includes unit/device tests where appropriate; website validation includes build, links and accessibility; backend validation includes authorization, persistence and API compatibility; infrastructure validation includes syntax/plan and reviewed deployment/restore evidence. Do not label those checks as available until implemented.
