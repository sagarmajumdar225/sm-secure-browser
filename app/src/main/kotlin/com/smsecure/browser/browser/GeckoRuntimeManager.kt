package com.smsecure.browser.browser

import android.content.Context
import com.smsecure.browser.privacy.ContentBlockingMode
import com.smsecure.browser.privacy.DnsConfig
import com.smsecure.browser.privacy.EnginePrivacyConfig
import com.smsecure.browser.privacy.FingerprintDefense
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import timber.log.Timber

object GeckoRuntimeManager {
    private var runtime: GeckoRuntime? = null

    fun getRuntime(
        context: Context,
        adBlockingEnabled: Boolean = true,
        torEnabled: Boolean = false
    ): GeckoRuntime {
        if (runtime == null) {
            try {
                val mode = if (adBlockingEnabled) ContentBlockingMode.STRICT else ContentBlockingMode.OFF
                val settingsBuilder = GeckoRuntimeSettings.Builder()
                    // API: GeckoRuntimeSettings.Builder.arguments - RFP, HTTPS-Only, Cert Pinning, Tor Proxy
                    .arguments(FingerprintDefense.getProcessArguments(torEnabled))
                    // API: GeckoRuntimeSettings.Builder.contentBlocking - ETP Strict, dFPI, cookie purging
                    .contentBlocking(EnginePrivacyConfig.createContentBlockingSettings(mode))
                    // API: GeckoRuntimeSettings.Builder.aboutConfigEnabled
                    .aboutConfigEnabled(true)

                // API: GeckoRuntimeSettings.Builder.trustedRecursiveResolverMode - DoH Mode 3
                DnsConfig.configureDoH(settingsBuilder)

                runtime = GeckoRuntime.create(context.applicationContext, settingsBuilder.build())
            } catch (e: Exception) {
                Timber.e(e, "Failed to create GeckoRuntime with custom settings, falling back to default runtime")
                runtime = GeckoRuntime.getDefault(context.applicationContext)
            }
        }
        return runtime!!
    }

    fun updateContentBlockingMode(adBlockingEnabled: Boolean) {
        val mode = if (adBlockingEnabled) ContentBlockingMode.STRICT else ContentBlockingMode.OFF
        try {
            runtime?.settings?.contentBlocking?.let { cbSettings ->
                val updated = EnginePrivacyConfig.createContentBlockingSettings(mode)
                cbSettings.enhancedTrackingProtectionLevel = updated.enhancedTrackingProtectionLevel
                cbSettings.cookieBehavior = updated.cookieBehavior
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update content blocking mode")
        }
    }
}
