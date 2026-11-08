# SM Secure Browser

**SM Secure Browser** is an open-source, zero-telemetry Android web browser engineered for absolute privacy and local data sovereignty. Built with Mozilla GeckoView, Jetpack Compose, and SQLCipher encrypted local storage.

## Key Privacy Features
- **Zero Telemetry / Zero Tracking**: Zero analytics SDKs, zero crash reporters, zero Mozilla telemetry, zero cloud tracking.
- **Enhanced Tracking Protection (ETP Strict)**: Blocks all known ad trackers, cryptomining scripts, and social media trackers by default.
- **Total Cookie Protection (dFPI)**: Isolates cookies per registrable domain to prevent cross-site tracking.
- **Resist Fingerprinting (RFP)**: Standardizes Canvas, WebGL, navigator, screen resolution, audio context, and timezone fingerprinting vectors.
- **DNS-over-HTTPS (DoH)**: Uses Quad9 filtering resolver exclusively (`TRR_MODE_ONLY`) with zero plaintext fallback.
- **Encrypted Local Storage**: History and Bookmarks stored in Room encrypted with SQLCipher via Android KeyStore (AES-256 GCM).
- **One-Tap Burn Session**: Instant wipe of memory, cookies, local storage, and cache.

## Build Instructions
### Prerequisites
- JDK 17
- Android SDK (API Level 35, Min SDK 26)
- Gradle 8.10.2

### Building Debug APK
```bash
./gradlew assembleDebug
```

### Building Release APK
```bash
./gradlew assembleRelease
```

### Verification & Audit
Run the automated privacy audit and unit test suite:
```bash
./gradlew testDebugUnitTest
```

## License
This project is licensed under the Mozilla Public License 2.0 (MPL-2.0). See [LICENSE](LICENSE) for details.
