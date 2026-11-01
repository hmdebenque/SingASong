"""Check repository Markdown file links and basic whitespace, without network access."""

from pathlib import Path
import re
import sys
from urllib.parse import unquote, urlsplit


ROOT = Path(__file__).resolve().parents[1]
SKIP = {".git", ".gradle", ".venv", "node_modules", "build", "dist"}
LINK = re.compile(r"!?\[[^\]]*\]\(([^)]+)\)")


def main():
    errors = []
    files = [p for p in ROOT.rglob("*.md") if not SKIP.intersection(p.relative_to(ROOT).parts)]
    for path in sorted(files):
        content = path.read_text(encoding="utf-8")
        relative = path.relative_to(ROOT)
        if content and not content.endswith("\n"):
            errors.append(f"{relative}: missing final newline")
        fence = None
        for number, line in enumerate(content.splitlines(), 1):
            if line.rstrip() != line:
                errors.append(f"{relative}:{number}: trailing whitespace")
            stripped = line.lstrip()
            marker = re.match(r"(`{3,}|~{3,})", stripped)
            if marker:
                token = marker.group(1)
                if fence is None:
                    fence = token
                elif token[0] == fence[0] and len(token) >= len(fence):
                    fence = None
                continue
            if fence:
                continue
            for match in LINK.finditer(line):
                target = match.group(1).strip()
                if target.startswith("<") and ">" in target:
                    target = target[1:target.index(">")]
                else:
                    target = target.split(' "', 1)[0]
                parsed = urlsplit(target)
                if parsed.scheme or parsed.netloc or not parsed.path:
                    continue
                resolved = (path.parent / unquote(parsed.path)).resolve()
                if not resolved.exists():
                    errors.append(f"{relative}:{number}: missing local target {target}")
    if errors:
        print("\n".join(errors), file=sys.stderr)
        return 1
    print(f"Checked {len(files)} Markdown files: local file links and whitespace passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
