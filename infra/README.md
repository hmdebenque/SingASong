# Shared infrastructure

Status: planned; provider, region and IaC tool unselected.

Root modules/ will hold reusable infrastructure definitions; environments/ will compose development, staging and production with component-owned resources. Do not create unused environments or services merely to populate the tree.

Scope includes identity/DNS/shared resources as needed, secrets and CI identity, backups, observability, budgets and recovery. Component resources live beside their owner; workflows orchestrate deployments. See the [roadmap](../ROADMAP.md), [ownership](../docs/architecture/repository.md) and SAS-004/007/012/020 in the [backlog](../docs/project/backlog.md).
