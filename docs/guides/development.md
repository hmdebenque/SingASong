# Development and validation

The reboot provides documentation and a Python documentation check. The preserved legacy Android application is in `app/`, with its original root Gradle build and wrapper. It has not been built or tested in this integration; SAS-002/006 must audit its compatibility before selecting a new build foundation. No Android SDK/JDK, Node manager, backend compiler, mise configuration or cloud tool is selected by this scaffold.

Run from the repository root:

```sh
python3 scripts/check_docs.py
```

The check verifies local Markdown inline-link file targets, trailing whitespace and final newlines in Markdown files. It skips external URLs, headings and code blocks; it does not validate remote links, Markdown anchors, legal correctness or application behaviour. Use `git diff --check` once the workspace is a Git checkout.

During SAS-007, consider dsr-engine's mise pattern: one version/task registry with component-specific build/lint/test tasks and an aggregate check. Select actual supported tool versions with the component bootstrap; do not copy dsr-engine's Rust/Node pins. Keep the Android Gradle wrapper and commit applicable application lockfiles. Check tasks must not implicitly provision cloud resources or install unrelated toolchains.

When runnable, each component must document prerequisites and exact commands in its README and connect relevant checks to CI. Plan separate Android, website, backend and infrastructure jobs plus contract/consumer checks for shared-interface changes. Run all relevant component jobs for cross-cutting changes. Publishing/deployment jobs are separate from pull-request validation and use protected credentials/environments.

Android validation includes unit/device tests where appropriate; website validation includes build, links and accessibility; backend validation includes authorization, persistence and API compatibility; infrastructure validation includes syntax/plan and reviewed deployment/restore evidence. Do not label those checks as available until implemented.
