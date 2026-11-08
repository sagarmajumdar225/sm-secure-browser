# CODING-STANDARDS.md

## Language & Style
- Kotlin official style guide (ktlint)
- All user-facing strings in strings.xml (ready for localization)
- No `!!`; prefer `?.` / `requireNotNull()` with message
- Compose: one screen per file, state hoisted, ViewModels per screen

## The Privacy Rules (violations = immediate revert)
1. NEVER add: analytics, ads, Firebase, Crashlytics, sentry, install-referrer, 
   or any SDK that phones home.
2. NEVER log URLs, browsing history, or user content.
3. NEVER make a network request outside a user-initiated page load.
4. Every new dependency: comment in gradle explaining why it's safe 
   ("no network calls", verified source).
5. New Android permissions require explicit human approval in PR review.

## Git Conventions
- Commits: `feature: ...`, `fix: ...`, `privacy: ...`, `release: ...`
- One feature/fix per commit; CI must pass before merge
- Branches: `main` (stable), `dev` (integration), feature branches

## AI Agent Instructions
- Read PROJECT.md, THREAT-MODEL.md, MVP-SPEC.md before any task
- Only implement UNCHECKED items in MVP-SPEC.md
- After implementing: build the project, fix compile errors, report what 
  you changed and which GeckoView APIs you used
- Check off completed items in MVP-SPEC.md
- If a task requires weakening a privacy default: STOP and ask the human
