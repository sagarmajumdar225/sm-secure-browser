# PROJECT.md — SM Secure Browser Vision & Master Plan

## What This Is
SM Secure Browser is a free, open-source, privacy-first Android browser built on 
GeckoView (Mozilla's engine). Our core promise: **verifiable zero tracking** 
— not marketing, but a technically defensible architecture.

## Non-Negotiables (apply to every commit)
1. ZERO telemetry, analytics, crash auto-reporting, or tracking SDKs. Ever.
2. No network calls outside of the user's explicit browsing activity.
3. Every dependency must be justified with a code comment. No silent additions.
4. License: MPL 2.0 (mandatory due to Gecko/Mozilla code usage).
5. User data (history, bookmarks) stays on-device, encrypted. No cloud sync.
6. Strict protection is the DEFAULT. Weakening requires explicit user action.
7. Minimize Android permissions. Currently required: INTERNET only (+storage 
   for downloads if implemented).

## Target Platforms (in order)
1. Android (this project) — minSdk 26, targetSdk 35
2. Windows desktop (future project, same engine: Gecko)

## Roadmap (source of truth for phases)
- Phase 1: Skeleton browser (tabs, navigation, local history/bookmarks)
- Phase 2: Zero-tracking layer (ETP Strict, dFPI, DoH, RFP, per-site rules)
- Phase 3: Polish & RAM optimization (tab suspension, burn session)
- Phase 4: Testing & hardening (leak tests, site compatibility, alpha)
- Phase 5: Release (F-Droid, GitHub Releases, Play Store)

## Definition of "Zero Tracking" (technical, testable)
A browsing session where:
- No third-party cookies or storage (all storage partitioned per first-party)
- No third-party network requests by default (blocklists + strict mode)
- No fingerprintable device identity (RFP enabled: canvas, WebGL, navigator, 
  timezone, screen)
- No DNS, WebRTC, or referrer leaks
- No telemetry from the app or engine

## Verification Protocol
After every privacy phase, test manually against:
- https://coveryourtracks.eff.org/ → must show "randomized fingerprint"
- https://ipleak.net/ → no DNS/WebRTC leaks
- https://www.dnsleaktest.com/ → only configured DoH resolver visible
Record results in /docs/test-results.md with screenshots.

## Definition of Done (per feature)
- Code compiles, builds via CI, reviewed by a human
- No new permission or dependency without justification comment
- Feature documented in MVP-SPEC.md (checkbox checked)
