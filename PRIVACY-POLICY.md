# Privacy Policy — SM Secure Browser

**Last updated:** November 2026

SM Secure Browser is designed from the ground up with a **zero-data collection architecture**.

## 1. No Data Collection
- **We do not collect, transmit, or store any personal data.**
- We operate **zero servers**, **zero analytics platforms**, and **zero cloud infrastructure**.
- There are **no user accounts**, **no tracking identifiers**, and **no crash reporters** (no Firebase, no Crashlytics, no Sentry, no Glean).

## 2. On-Device Encrypted Data
- All user data (browsing history, bookmarks, per-site exceptions) stays strictly on your device.
- Local databases are encrypted using **SQLCipher (AES-256)** with keys secured by the hardware-backed **Android KeyStore**.

## 3. Network & DNS Security
- DNS queries are encrypted using **DNS-over-HTTPS (DoH)** via Quad9 (`dns.quad9.net`) in **exclusive mode (Mode 3)**, preventing unencrypted DNS leaks to your ISP.
- Cross-site HTTP referrers are automatically stripped.
- WebRTC IP address leaks are disabled by default.

## 4. One-Tap Data Wiping
- Using the **Burn Session** feature immediately purges all active session memory, cookies, local storage, and cached network responses from your device.

## 5. Contact
Because we do not collect any user data or operate servers, we have no user data to share, inspect, or delete.
