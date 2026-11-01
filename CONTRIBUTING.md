# Contributing

Start with the [backlog](docs/project/backlog.md) and relevant [decisions](docs/decisions/README.md). Use the [task template](.github/ISSUE_TEMPLATE/task.md) to describe the observable outcome, dependencies, boundaries and acceptance criteria before substantial implementation.

Keep one coherent deliverable per change. Cross-component work may live in one PR when it delivers one compatible workflow; identify affected components and validate each. Use the [PR template](.github/pull_request_template.md) to record evidence, migrations and release impact.

Follow the [development guide](docs/guides/development.md). No hooks, package managers, cloud resources or GitHub projects are automatically installed or configured by this scaffold. The original Git history is integrated; legacy source remains available there, while SAS-022 supplies the active modern Android foundation. SAS-001 is complete under the maintainer-approved local-tracking scope; the backlog remains authoritative. See the [integration report](docs/project/sas-001-integration.md).

Record significant architecture/product choices using the [decision template](docs/decisions/template.md). Keep proposals distinct from maintainer-approved decisions. Record documentation milestones in the [knowledge log](docs/log.md), without duplicating routine commit history.
