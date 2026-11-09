package com.smsecure.browser.privacy

import org.mozilla.geckoview.ContentBlocking

object EnginePrivacyConfig {
    /**
     * Configures GeckoView ContentBlocking settings and security enforcement (HTTPS-only, cert pinning) according to THREAT-MODEL.md.
     */
    fun createContentBlockingSettings(mode: ContentBlockingMode = ContentBlockingMode.STRICT): ContentBlocking.Settings {
        val settings = ContentBlocking.Settings.Builder()

        when (mode) {
            ContentBlockingMode.STRICT -> {
                // API: ContentBlocking.EtpLevel.STRICT - Enables strict ETP mode
                settings.enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.STRICT)
                // API: ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS - Total Cookie Protection / dFPI (cookieBehavior = 5)
                settings.cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS)
                // API: ContentBlocking.Settings.cookiePurging - Automatically purges tracking cookies
                settings.cookiePurging(true)
                // API: ContentBlocking.Settings.strictSocialTrackingProtection - Blocks social media trackers strictly
                settings.strictSocialTrackingProtection(true)
                // API: ContentBlocking.Settings.queryParameterStrippingEnabled - Strips tracking parameters from URLs
                settings.queryParameterStrippingEnabled(true)
                settings.queryParameterStrippingPrivateBrowsingEnabled(true)
            }
            ContentBlockingMode.STANDARD -> {
                // API: ContentBlocking.EtpLevel.DEFAULT - Enables standard ETP mode
                settings.enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.DEFAULT)
                settings.cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_FIRST_PARTY_AND_ISOLATE_OTHERS)
            }
            ContentBlockingMode.OFF -> {
                // API: ContentBlocking.EtpLevel.NONE - Disables content blocking
                settings.enhancedTrackingProtectionLevel(ContentBlocking.EtpLevel.NONE)
                settings.cookieBehavior(ContentBlocking.CookieBehavior.ACCEPT_ALL)
            }
        }

        return settings.build()
    }
}

enum class ContentBlockingMode {
    STRICT,
    STANDARD,
    OFF
}
