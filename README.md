# SM Secure Browser

SM Secure Browser is a free, open-source, privacy-first Android web browser built on Mozilla's **GeckoView** engine, engineered by **SM SECURE STUDIO**.

> **Core Promise**: Verifiable zero tracking, zero telemetry, zero crash auto-reporting, and zero cloud sync. All history, bookmarks, and per-site rules stay encrypted on your device using SQLCipher (AES-256).

[![Latest Release](https://img.shields.io/github/v/release/sagarmajumdar225/sm-secure-browser?label=Download%20APK%20v1.0.0&color=00A896)](https://github.com/sagarmajumdar225/sm-secure-browser/releases/latest)
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://www.mozilla.org/en-US/MPL/2.0/)
[![Website](https://img.shields.io/badge/Website-sagarmajumdar.in-0A66C2)](https://sagarmajumdar.in)

---

## 📲 How to Download & Install

1. **Go to GitHub Releases**:
   Visit the [**Latest Releases Page**](https://github.com/sagarmajumdar225/sm-secure-browser/releases/latest).

2. **Select the APK for your Device Architecture**:
   Under **Assets**, download the APK file for your phone:
   - 🌟 **`app-arm64-v8a-release.apk`** (**~75 MB**): Recommended for **90%+ of modern Android phones & tablets**.
   - **`app-armeabi-v7a-release.apk`** (**~72 MB**): For older 32-bit Android phones.
   - **`app-x86_64-release.apk`** (**~78 MB**): For Chromebooks or Intel Android devices.

3. **Install on Android**:
   Open the downloaded `.apk` file on your Android device and tap **Install** *(if prompted, allow installation from unknown sources in your browser or file manager settings)*.

---

## 📦 Optimized Package Sizes (ABI Splits)

Rather than bundling a ~600 MB universal APK containing all 4 CPU architectures, SM Secure Browser utilizes **ABI Splits** to produce standalone, per-architecture APKs:

| Architecture / Artifact | Target Devices | Size |
|---|---|---|
| **`app-arm64-v8a-release.apk`** | 90%+ of modern Android phones & tablets | **~75 MB** |
| **`app-armeabi-v7a-release.apk`** | Legacy 32-bit ARM Android devices | **~72 MB** |
| **`app-x86_64-release.apk`** | Intel-based Chromebooks & emulators | **~78 MB** |

---

## 🛡️ Protection Matrix & GeckoView API Mapping

Every privacy defense is technically enforced in the Gecko rendering engine layer:

| Defense / Feature | Implementation Strategy | GeckoView API / Preference Citation |
|---|---|---|
| **Enhanced Tracking Protection (STRICT Mode)** | Block all tracking categories (ads, analytics, social, cryptomining, fingerprinting) | `ContentBlocking.Settings.Builder.enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.STRICT)`, `antiTracking(...)`, `strictSocialTrackingProtection(true)`, `cookiePurging(true)` |
| **Total Cookie Protection (dFPI)** | First-party storage isolation (cookies, local storage, cache partitioned per top-level domain) | `ContentBlocking.Settings.Builder.cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS)`, `cookieBehaviorPrivateMode(...)` |
| **DNS-over-HTTPS (DoH)** | Filtering resolver with fallback-off (TRR-only mode; no plaintext DNS fallback) | `GeckoRuntimeSettings.Builder.trustedRecursiveResolverMode(GeckoRuntimeSettings.TRR_MODE_ONLY)`, `trustedRecursiveResolverUri("https://mozilla.cloudflare-dns.com/dns-query")` |
| **WebRTC IP Leak Defense** | Proxy-only ICE candidates, host candidate discovery disabled | `GeckoRuntimeSettings.Builder.arguments(arrayOf("--pref", "media.peerconnection.ice.default_address_only=true", "--pref", "media.peerconnection.ice.no_host=true"))` |
| **Cross-Site Referrer Stripping** | Trim cross-site HTTP referrer headers to origin only | `GeckoRuntimeSettings.Builder.arguments(arrayOf("--pref", "network.http.referer.trimmingPolicy=2", "--pref", "network.http.referer.XOriginTrimmingPolicy=2"))` |
| **Resist Fingerprinting (RFP)** | Canvas read noise, WebGL vendor/renderer spoofing, navigator surface reduction, UTC/en-US masking, screen resolution bucketing | `GeckoRuntimeSettings.Builder.arguments(arrayOf("--pref", "privacy.resistFingerprinting=true", "--pref", "privacy.resistFingerprinting.randomDataOnCanvasExtract=true", "--pref", "javascript.use_us_english_locale=true", "--pref", "webgl.enable-debug-renderer-info=false"))` |
| **Session-Only Storage Default** | Cookies & storage set to session-only lifetime policy; auto-clear on shutdown | `GeckoRuntimeSettings.Builder.arguments(arrayOf("--pref", "network.cookie.lifetimePolicy=2", "--pref", "privacy.sanitize.sanitizeOnShutdown=true"))` |
| **Zero Telemetry & Crash Reporting** | Unattach all streaming telemetry delegates and crash reporters | `GeckoRuntimeSettings.Builder.telemetryDelegate(null)`, `crashHandler(null)`, `aboutConfigEnabled(false)` |
| **One-Tap Burn Session** | Instantly purge all active sessions, cookies, network cache, DOM storage, and site permissions | `StorageController.clearData(StorageController.ClearFlags.ALL)` |
| **Per-Site Exceptions** | Site-specific toggles stored locally in encrypted Room database keyed by registrable domain | `PublicSuffixList.getRegistrableDomain(url)`, `GeckoSession.settings.useTrackingProtection`, `GeckoSession.settings.allowJavascript` |

---

## 🏗️ Building & Signing

### Prerequisites
- JDK 17
- Android SDK 35 (minSdk 26)
- Gradle 8.10.2

### Local Debug Build
```bash
./gradlew assembleDebug
```

### Local Signed Release Build
Configure your release key details in `local.properties` (this file is excluded from Git):
```properties
RELEASE_STORE_FILE=/path/to/keystore.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=your_key_alias
RELEASE_KEY_PASSWORD=your_key_password
```
Then build the release APKs:
```bash
./gradlew assembleRelease
```
The resulting per-architecture APKs will be output to `app/build/outputs/apk/release/`.

### Automated Privacy Audit & Unit Tests
```bash
./gradlew testDebugUnitTest
```

---

## 📬 Contact & Support

Built and maintained by **Sagar Majumdar** (SM SECURE STUDIO).

- 🌐 **Website**: [sagarmajumdar.in](https://sagarmajumdar.in)
- 📧 **Support & Security Inquiries**: support@sagarmajumdar.in
- 🐛 **Issue Tracker**: [GitHub Issues](https://github.com/sagarmajumdar225/sm-secure-browser/issues)

---

## 📜 License

This project is licensed under the **Mozilla Public License 2.0 (MPL 2.0)**. See [LICENSE](LICENSE) for details.
