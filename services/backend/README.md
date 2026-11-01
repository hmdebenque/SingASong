# Backend

Status: planned; Firebase versus dedicated AWS and Java/Go/Rust remain open.

Owns choir membership/permissions, repertoire metadata, programme revisions, private media authorization, synchronization and data lifecycle. Future contracts/, migrations/ and infra/ directories belong beside implementation; create only what the chosen architecture needs. A Firebase selection still requires explicit ownership for rules, functions, indexes and data migrations.

See [decision 0003](../../docs/decisions/0003-backend-and-hosting.md), [ownership](../../docs/architecture/repository.md) and SAS-004/012/013/014/020 in the [backlog](../../docs/project/backlog.md). Document build/run/test and contract-generation commands at bootstrap. Database credentials and privileged access never belong in Android or website clients.
