package com.smsecure.browser.browser

import android.content.Context
import com.smsecure.browser.privacy.ContentBlockingMode
import com.smsecure.browser.privacy.DnsConfig
import com.smsecure.browser.privacy.EnginePrivacyConfig
import com.smsecure.browser.privacy.FingerprintDefense
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings

object GeckoRuntimeManager {
    private var runtime: GeckoRuntime? = null

    fun getRuntime(context: Context, mode: ContentBlockingMode = ContentBlockingMode.STRICT): GeckoRuntime {
        if (runtime == null) {
            val settingsBuilder = GeckoRuntimeSettings.Builder()
                // API: GeckoRuntimeSettings.Builder.arguments - RFP, WebRTC proxy-only, anti-telemetry
                .arguments(FingerprintDefense.getProcessArguments())
                // API: GeckoRuntimeSettings.Builder.contentBlocking - ETP Strict, dFPI, cookie purging
                .contentBlocking(EnginePrivacyConfig.createContentBlockingSettings(mode))
                // API: GeckoRuntimeSettings.Builder.aboutConfigEnabled
                .aboutConfigEnabled(true)

            // API: GeckoRuntimeSettings.Builder.trustedRecursiveResolverMode - DoH Mode 3
            DnsConfig.configureDoH(settingsBuilder)

            runtime = GeckoRuntime.create(context.applicationContext, settingsBuilder.build())
        }
        return runtime!!
    }

    fun updateContentBlockingMode(mode: ContentBlockingMode) {
        runtime?.settings?.contentBlocking?.let { cbSettings ->
            val updated = EnginePrivacyConfig.createContentBlockingSettings(mode)
            cbSettings.enhancedTrackingProtectionLevel = updated.enhancedTrackingProtectionLevel
            cbSettings.cookieBehavior = updated.cookieBehavior
        }
    }
}
