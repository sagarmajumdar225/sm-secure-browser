package com.smsecure.browser.util

import android.net.Uri

object UrlSanitizer {
    private val TRACKING_PARAMS = setOf(
        "fbclid", "gclid", "dclid", "msclkid",
        "utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content",
        "_hsenc", "_hsmi", "mc_cid", "mc_eid",
        "wickedid", "fb_action_ids", "fb_action_types", "fb_source"
    )

    fun sanitizeUrl(rawUrl: String): String {
        try {
            val uri = Uri.parse(rawUrl) ?: return rawUrl
            if (uri.queryParameterNames.none { it.lowercase() in TRACKING_PARAMS }) {
                return rawUrl
            }

            val builder = uri.buildUpon().clearQuery()
            for (param in uri.queryParameterNames) {
                if (param.lowercase() !in TRACKING_PARAMS) {
                    for (value in uri.getQueryParameters(param)) {
                        builder.appendQueryParameter(param, value)
                    }
                }
            }
            return builder.build().toString()
        } catch (e: Exception) {
            return rawUrl
        }
    }
}
