# ARCHITECTURE.md — Structure & Conventions

## Tech Stack
- Language: Kotlin (JVM), Jetpack Compose for all UI (no XML views)
- Engine: GeckoView (org.mozilla.geckoview — latest stable from Mozilla Maven)
- Storage: Room + SQLCipher (encrypted SQLite)
- DI: manual (keep it simple — no Hilt unless complexity demands it)
- Async: Kotlin Coroutines + Flow
- Min SDK 26, Target SDK 35, Java 17

## Package Structure
com.smsecure.browser
├── app/ → MainActivity, Compose screens, navigation, themes
├── browser/ → GeckoView session wrapper, tab manager, navigation state
├── privacy/ → engine privacy config, per-site rules, exception store
├── data/ → Room entities, DAOs, database (history, bookmarks, exceptions)
├── settings/ → settings screen + DataStore persistence
└── util/ → public suffix list, domain helpers, formatters


## Key Design Rules
1. **All GeckoView configuration lives in `privacy/`** — one file per concern
   (e.g., `EnginePrivacyConfig.kt`, `FingerprintDefense.kt`, `DnsConfig.kt`).
   Never scatter engine prefs across UI code.
2. **UI never touches GeckoView directly** — goes through `browser/` layer.
3. **Every GeckoView API call gets a comment** citing the API/pref name.
4. **No dependency without a justification comment** in the build.gradle block.
5. **No logging of URLs or user data** in release builds (use Timber with 
   release tree that drops logs, or BuildConfig guards).

## State Model
- One `GeckoSession` per tab. Tab = data class (id, title, url, state, suspended).
- Tab list state in a ViewModel (StateFlow). Survives process death via 
  session state serialization to local storage.

## Testing Conventions
- Unit tests: domain logic (exception matching, PSL handling)
- Manual test protocol: see PROJECT.md verification section
