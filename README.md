# SM Secure Browser

SM Secure Browser is a free, open-source, privacy-first Android web browser built on Mozilla's **GeckoView** engine, engineered by **SM SECURE STUDIO**.

> **Core Promise**: Verifiable zero tracking, zero telemetry, zero crash auto-reporting, and zero cloud sync. All history, bookmarks, passwords, and per-site rules stay encrypted on your device using SQLCipher (AES-256).

[![Latest Release](https://img.shields.io/github/v/release/sagarmajumdar225/sm-secure-browser?label=Download%20APK%20v1.0.0&color=00A896)](https://github.com/sagarmajumdar225/sm-secure-browser/releases/latest)
[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://www.mozilla.org/en-US/MPL/2.0/)
[![Website](https://img.shields.io/badge/Website-sagarmajumdar.in-0A66C2)](https://sagarmajumdar.in)

---

## 🚀 Advanced Security & Privacy Features

1. **Native Ad-Blocking & Anti-Tracking (`ContentBlocking`)**:
   - Leverages GeckoView's native `ContentBlocking` API to block ads, analytics trackers, social scripts, and cryptominers by default.
   - Includes a user-facing toggle switch in **Settings** to enable or relax anti-tracking rules on demand.

2. **Window Screenshot & Task Switcher Protection (`FLAG_SECURE`)**:
   - Enforces `WindowManager.LayoutParams.FLAG_SECURE` inside `MainActivity`, preventing background screenshots, screen recordings, and recent app switcher thumbnail leaks.

3. **Local Encrypted Storage (SQLCipher + Android KeyStore)**:
   - History, Bookmarks, and Encrypted Password Manager logins are stored locally in Room databases encrypted with **SQLCipher (AES-256)**.
   - Database passphrases are securely generated and wrapped using hardware-backed Android KeyStore encryption.

4. **Orbot / Tor SOCKS5 Proxy Routing**:
   - Optional network toggle in Settings to route all browser traffic and remote DNS queries through a local SOCKS5 proxy (`localhost:9050` for Orbot/Tor) to prevent IP and DNS leaks.

5. **HTTPS-Only Mode & Certificate Pinning**:
   - Enforces strict HTTPS-Only mode (`dom.security.https_only_mode=true`) and certificate pinning enforcement (`security.cert_pinning.enforcement_level=2`) to block unauthorized MITM inspection proxies.

6. **Settings Panel Footer Branding**:
   - Features a clean custom footer at the bottom of the Settings panel: *"Made and secured by SM Secure Studio"*, which instantly opens [sagarmajumdar.in](https://sagarmajumdar.in) in the browser when tapped.

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

| Defense / Feature | Implementation Strategy | GeckoView API / Preference Citation |
|---|---|---|
| **Ad-Blocking & Anti-Tracking** | Block ads, analytics, social scripts, cryptominers with settings toggle | `ContentBlocking.Settings.Builder.enhancedTrackingProtectionLevel`, `antiTracking(...)`, `cookieBehavior(...)` |
| **FLAG_SECURE Window Protection** | Prevent screenshots and recent task thumbnails leaks | `window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)` |
| **Encrypted Password Manager** | Local encrypted credential store with autofill support | Room + SQLCipher + Android KeyStore AES-256 |
| **Tor / Orbot SOCKS5 Proxy** | Route web traffic and remote DNS over localhost:9050 | `network.proxy.type=1`, `network.proxy.socks=127.0.0.1`, `network.proxy.socks_port=9050`, `network.proxy.socks_remote_dns=true` |
| **HTTPS-Only & Cert Pinning** | Enforce secure transport and block MITM proxies | `dom.security.https_only_mode=true`, `security.cert_pinning.enforcement_level=2` |
| **Total Cookie Protection (dFPI)** | First-party storage isolation partitioned per domain | `ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS` |
| **DNS-over-HTTPS (DoH)** | Filtering resolver with fallback-off (Quad9 TRR mode 3) | `GeckoRuntimeSettings.Builder.trustedRecursiveResolverMode(GeckoRuntimeSettings.TRR_MODE_ONLY)` |
| **WebRTC IP Leak Defense** | Proxy-only ICE candidates, host discovery disabled | `media.peerconnection.enabled=false`, `media.peerconnection.ice.no_host=true` |
| **Resist Fingerprinting (RFP)** | Canvas noise, WebGL spoofing, screen bucketing, timezone masking | `privacy.resistFingerprinting=true` |
| **One-Tap Burn Session** | Instantly purge all active sessions, cookies, and cache | `StorageController.clearData(StorageController.ClearFlags.ALL)` |

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
Configure your release key details in `local.properties` (excluded from Git):
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

Built and maintained by **Sagar Majumdar** (**SM SECURE STUDIO**).

- 🌐 **Website**: [sagarmajumdar.in](https://sagarmajumdar.in)
- 📧 **Support & Security Inquiries**: support@sagarmajumdar.in
- 🐛 **Issue Tracker**: [GitHub Issues](https://github.com/sagarmajumdar225/sm-secure-browser/issues)

---

## 📜 License

This project is licensed under the **Mozilla Public License 2.0 (MPL 2.0)**. See [LICENSE](LICENSE) for details.
