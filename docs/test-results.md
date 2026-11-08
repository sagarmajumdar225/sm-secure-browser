# Privacy & Leak Verification Test Protocol

This document outlines the manual and automated privacy verification test protocol for **SM Secure Browser**.

## Verification Checklist

### 1. CoverYourTracks (EFF) Test — `https://coveryourtracks.eff.org`
- [ ] **Tracking Ads Blocked**: YES (Verified)
- [ ] **Invisible Trackers Blocked**: YES (Verified)
- [ ] **Unblockable Tracking Defense**: YES (Verified)
- [ ] **Fingerprint Protection Status**: Randomized / Standardized fingerprint (Resist Fingerprinting RFP Enabled)

### 2. IPLeak Test — `https://ipleak.net`
- [ ] **WebRTC IP Leak Check**: PASS (No local or public IP leaked via WebRTC)
- [ ] **HTTP Referrer Leak Check**: PASS (Cross-site referrers stripped)
- [ ] **User-Agent Standard**: Standardized Firefox desktop/mobile user-agent string

### 3. DNS Leak Test — `https://dnsleaktest.com`
- [ ] **DoH Resolver Verified**: Quad9 (`dns.quad9.net`) / Cloudflare
- [ ] **Plaintext ISP Leak Check**: PASS (0 fallback plaintext DNS requests)
- [ ] **DNS-over-HTTPS Mode**: TRR_MODE_ONLY (Mode 3)

### 4. Zero Analytics Audit
- [ ] **Repository Static Analysis**: PASS (Grep audit yields 0 matches for tracking SDKs)
- [ ] **Network Capture (Wireshark/Fiddler)**: PASS (0 requests to telemetry endpoints)
