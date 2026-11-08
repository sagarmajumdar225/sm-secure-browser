# THREAT-MODEL.md — What We Block and Why

## Who We Defend Against
1. **Trackers/ad networks** — cookies, scripts, pixels, CNAME cloaking
2. **Fingerprinters** — canvas, WebGL, fonts, audio, navigator probing
3. **Network observers (ISPs)** — plaintext DNS, referrers, unencrypted traffic
4. **The app itself** — telemetry, accidental data collection (supply chain)

## Who We Do NOT Defend Against (be honest)
- The user's ISP seeing IP connections to sites (unless user enables proxy/Tor mode)
- Malicious websites exploiting Gecko 0-days (mitigated by fast upstream merges)
- Compromised devices

## Tracking Vectors & Our Countermeasures

| # | Vector | Countermeasure | Implementation |
|---|--------|----------------|----------------|
| 1 | Third-party cookies | Block all 3rd-party cookies | GeckoView ETP Strict |
| 2 | Cross-site storage correlation | Total Cookie Protection (dFPI) — partition all storage per top-level site | GeckoRuntimeSettings |
| 3 | Tracker scripts/requests | Blocklist filtering (EasyList, EasyPrivacy, Disconnect) | Gecko content blocking categories |
| 4 | Canvas fingerprinting | Randomized noise on canvas reads | privacy.resistFingerprinting |
| 5 | WebGL fingerprinting | Spoof vendor/renderer | RFP |
| 6 | navigator/hardware probing | Reduced surface, rounded values | RFP |
| 7 | Timezone/locale leaks | Mask to UTC / en-US | RFP |
| 8 | Screen resolution | Fixed bucket reporting | RFP |
| 9 | Audio fingerprinting | Noise on AudioContext | RFP |
| 10 | DNS leaks (plaintext) | DNS-over-HTTPS by default, no plaintext fallback | Gecko DoH config |
| 11 | WebRTC IP leaks | Proxy-only mode / disable unless granted | Gecko WebRTC prefs |
| 12 | Referrer leakage | Strip cross-site referrers | Gecko pref |
| 13 | ETag/cache supercookies | Cache partitioning per first-party | dFPI + verify |
| 14 | App telemetry | All Glean/telemetry/crash-ping disabled & removed | engine config |
| 15 | Session persistence tracking | Session-only storage default; burn-session wipe | app layer |

## What Users Keep (by design)
- First-party logins (cookies/storage for the site itself, user-granted)
- Local history & bookmarks (on-device, encrypted)
- Downloads

## Exception Policy
Exceptions are ALWAYS user-explicit, per-site, and revocable. Defaults never weaken.
