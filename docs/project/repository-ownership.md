# Repository ownership — 2026-10-31

The maintainer confirmed ownership of the reboot repository and authorized resetting/restructuring it as needed to fit the reboot. Local `origin` was verified as `git@github.com:hmdebenque/SingASong.git` for fetch and push. The current repository is [hmdebenque/SingASong](https://github.com/hmdebenque/SingASong); `frenchfrie/SingASong` remains the historical source.

This supersedes the earlier assumption that the project must remain constrained by the old repository's ownership or pull-only tracker access. Those findings in SAS-001 are historical and must not be represented as current access checks. Actual remote write capability has not been tested by this update.

Legacy repository structure/history is no longer a required constraint on reboot design. Resetting is permitted when useful, not a pending operation that must happen automatically. No reset, branch rewrite, push or deletion was performed here. Preserve current uncommitted work and recoverable backups before any later restructuring, and coordinate with the active implementation session so changes are not lost.

Local backlog remains authoritative. New repository ownership does not reinstate GitHub tracker management, alter product scope or authorize Play publication/cloud deployment. The active SAS-008 implementation continues in the existing checkout; repository housekeeping need not interrupt it.
