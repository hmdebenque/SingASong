# 0003: Backend and hosting direction

- Status: Proposed
- Date: 2026-10-30
- Categories: backend, infrastructure, website
- Agreement/evidence: Pending architecture evaluation in SAS-004.

## Context and options

The [roadmap backend track](../../ROADMAP.md) compares Firebase managed services with a dedicated Java/Go/Rust API on AWS. The maintainer's experience makes AWS a credible working preference; operational cost and time to first hosted workflow must still be evaluated.

## Decision criteria

Evaluate the same invitation → publication → private download → revision → revocation → deletion workflow for both options. Compare implementation effort, tenant isolation, offline synchronization, region/data transfers, backup/restore, authentication, storage/egress and ongoing operations. Website hosting should support public legal/support URLs independently of account availability.

## Open choices

Provider, language, database, managed identity, hosting topology, region, IaC tool and monthly pilot budget. Also decide whether hosted updates are required for the MVP; this changes the dependency graph and release gate.

## Implementation status

No cloud resources provisioned. SAS-004 produces the decision and SAS-012 implements hosted foundations only when that scope is selected.
