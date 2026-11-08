# MVP-SPEC.md — v1.0 Release Feature Checklist
(AI agents: work top-down. Only do UNCHECKED items. Check off when done & building.)

## Core Browsing
- [x] GeckoView renders web pages in MainActivity
- [x] URL bar: type URL or search query (default engine: DuckDuckGo)
- [x] Navigation: back, forward, reload, stop
- [x] Page progress indicator
- [x] Tab management: open, close, switch; Compose tab tray
- [x] Background tabs don't reload on switch (state preserved)
- [x] Downloads (to public Downloads dir, with notification)

## Local Data (zero cloud)
- [x] History: stored in Room, local-only, list + delete UI
- [x] Bookmarks: Room, add/star from URL bar, list UI
- [x] All local data encrypted (SQLCipher or EncryptedFile)

## Privacy Engine (see THREAT-MODEL.md for details)
- [x] ETP Strict mode default (all blocking categories)
- [x] Total Cookie Protection / dFPI enabled
- [x] DoH enabled with filtering resolver; NO plaintext DNS fallback
- [x] WebRTC: proxy-only / disabled by default
- [x] Cross-site referrer stripped
- [x] Resist Fingerprinting enabled (canvas, WebGL, navigator, TZ, screen, audio)
- [x] Mozilla telemetry, Glean, crash pings: disabled & code-stripped
- [x] Session-only storage as default behavior

## Per-Site Control
- [x] Shield icon in URL bar with blocked-count for current site
- [x] Bottom sheet: per-site toggles (cookies, storage, scripts) 
- [x] Exceptions stored per registrable domain (public suffix list)
- [x] Global mode switch: Strict (default) / Standard / Off

## UX
- [x] "Burn session" button: wipes all session data instantly
- [x] Dark / light / system theme
- [x] Tab suspension: background tabs unloaded after 5 min
- [x] RAM usage shown per tab in tab tray

## Release
- [x] R8 minified release build + ProGuard rules for GeckoView
- [x] Signing via local.properties (keys never committed)
- [x] Reproducible build GitHub Actions workflow (signed APK artifact)
- [x] README.md + PRIVACY-POLICY.md generated from PROJECT.md/THREAT-MODEL.md
- [x] Repo audit: zero analytics/tracking SDKs (grep: firebase, analytics, ads, crashlytics)
