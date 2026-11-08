package com.smsecure.browser.privacy

import org.mozilla.geckoview.GeckoRuntimeSettings

object DnsConfig {
    /**
     * Configures DNS-over-HTTPS (DoH) with TRR_MODE_ONLY (Mode 3), guaranteeing zero plaintext DNS fallback.
     */
    fun configureDoH(builder: GeckoRuntimeSettings.Builder, resolverUri: String = "https://dns.quad9.net/dns-query") {
        // API: GeckoRuntimeSettings.TRR_MODE_ONLY - Enforces DoH exclusively without plaintext fallback
        builder.trustedRecursiveResolverMode(GeckoRuntimeSettings.TRR_MODE_ONLY)
        // API: GeckoRuntimeSettings.Builder.trustedRecursiveResolverUri - Sets filtering DoH resolver URI
        builder.trustedRecursiveResolverUri(resolverUri)
    }
}
