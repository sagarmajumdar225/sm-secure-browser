# AGENTS.md — Master Instructions for Android Studio Gemini Agent

## Context & Source of Truth
Before writing or modifying any code, always read and adhere to:
1. `PROJECT.md` — Vision, non-negotiables, and verification protocol.
2. `ARCHITECTURE.md` — Package structure (`com.smsecure.browser`), tech stack, and state model.
3. `CODING-STANDARDS.md` — Privacy rules, Kotlin conventions, and workflow.
4. `THREAT-MODEL.md` — Exact tracking vectors and GeckoView countermeasures.
5. `MVP-SPEC.md` — The master task checklist.

## Reference Subfolder Rule
- A local copy of the GeckoView source project exists in a subfolder in this workspace.
- DO NOT include the GeckoView source subfolder in `settings.gradle.kts` as a compiled subproject (this will break or severely slow down Gradle builds).
- Instead, configure `https://maven.mozilla.org/maven2/` in `settings.gradle.kts` and pull `org.mozilla.geckoview:geckoview-arm64-v8a` (or universal) via Maven, using the local GeckoView subfolder strictly as a read-only reference to verify exact API signatures and preference keys.

## Autonomous Execution Loop (Follow for every prompt)
1. Inspect `MVP-SPEC.md` and identify the next unchecked `[ ]` item(s) requested by the user.
2. Implement the code strictly inside the package boundaries defined in `ARCHITECTURE.md` (`app`, `browser`, `privacy`, `data`, `settings`, `util`).
3. Ensure every new dependency in `build.gradle.kts` has an inline comment justifying why it is safe and confirming it makes zero unauthorized network calls.
4. Run `./gradlew assembleDebug` and `./gradlew testDebugUnitTest` using the terminal tool.
5. If any compilation or test error occurs, read the stack trace, inspect the local GeckoView subfolder for accurate API usage if needed, fix the issue, and re-run the build until it passes.
6. Update `MVP-SPEC.md` by changing `[ ]` to `[x]` for verified items, and output a summary of modified files, GeckoView APIs/prefs used, and the suggested Git commit message (`feature:`, `privacy:`, `fix:`, or `release:`).