package com.smsecure.browser.privacy

object FingerprintDefense {
    /**
     * Returns Gecko process arguments to enforce RFP (Resist Fingerprinting), WebRTC IP leak defenses,
     * cross-site referrer stripping, and disabling all engine diagnostic and health report uploads.
     */
    fun getProcessArguments(): Array<String> {
        return arrayOf(
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
            "-pref", "privacy.globalprivacycontrol.enabled=true"
        )
    }
}
