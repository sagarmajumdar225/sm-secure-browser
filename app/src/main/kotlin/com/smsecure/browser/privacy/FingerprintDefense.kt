package com.smsecure.browser.privacy

object FingerprintDefense {
    /**
     * Returns Gecko process arguments to enforce RFP, HTTPS-Only mode, certificate pinning enforcement,
     * WebRTC IP leak defenses, referrer stripping, and Orbot/Tor SOCKS5 proxy routing.
     */
    fun getProcessArguments(torEnabled: Boolean = false): Array<String> {
        val baseArgs = listOf(
            // Pref: privacy.resistFingerprinting=true - Randomizes Canvas, WebGL, navigator, screen resolution, audio context noise, and UTC timezone
            "-pref", "privacy.resistFingerprinting=true",
            // Pref: network.http.referer.XOriginPolicy=2 - Strips cross-site referrers completely
            "-pref", "network.http.referer.XOriginPolicy=2",
            // Pref: media.peerconnection.enabled=false - Disables WebRTC peer connections to prevent IP address leaks
            "-pref", "media.peerconnection.enabled=false",
            // Pref: media.peerconnection.ice.no_host=true - Prevents WebRTC ICE candidate local IP disclosure
            "-pref", "media.peerconnection.ice.no_host=true",
            // Pref: toolkit.telemetry.enabled=false - Completely disables diagnostic reporting
            "-pref", "toolkit.telemetry.enabled=false",
            // Pref: datareporting.healthreport.uploadEnabled=false - Disables health report uploads
            "-pref", "datareporting.healthreport.uploadEnabled=false",
            // Pref: privacy.globalprivacycontrol.enabled=true - Signals Global Privacy Control (GPC) opt-out header
            "-pref", "privacy.globalprivacycontrol.enabled=true",
            // Pref: dom.security.https_only_mode=true - Enforces HTTPS-Only mode across all connections
            "-pref", "dom.security.https_only_mode=true",
            // Pref: security.cert_pinning.enforcement_level=2 - Enforces strict certificate pinning against MITM proxies
            "-pref", "security.cert_pinning.enforcement_level=2",
            // Pref: security.mixed_content.block_active_content=true - Blocks active mixed content on secure pages
            "-pref", "security.mixed_content.block_active_content=true"
        )

        val torArgs = TorProxyConfig.getProxyArguments(torEnabled).toList()
        return (baseArgs + torArgs).toTypedArray()
    }
}
