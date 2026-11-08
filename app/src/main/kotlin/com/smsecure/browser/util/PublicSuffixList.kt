package com.smsecure.browser.util

import java.net.URI

object PublicSuffixList {
    private val TWO_PART_TLDS = setOf(
        "co.uk", "org.uk", "me.uk", "gov.uk", "ac.uk",
        "com.au", "net.au", "org.au", "edu.au",
        "co.jp", "ne.jp", "or.jp", "go.jp",
        "co.nz", "net.nz", "org.nz",
        "com.br", "net.br", "org.br",
        "co.za", "org.za"
    )

    fun getRegistrableDomain(urlOrHost: String): String {
        val host = try {
            val uri = if (!urlOrHost.contains("://")) URI("https://$urlOrHost") else URI(urlOrHost)
            uri.host ?: urlOrHost
        } catch (e: Exception) {
            urlOrHost
        }.lowercase().trim()

        if (host.isBlank() || host.matches(Regex("^[0-9.]+$")) || host == "localhost") {
            return host
        }

        val parts = host.split(".")
        if (parts.size <= 2) {
            return host
        }

        val lastTwo = "${parts[parts.size - 2]}.${parts[parts.size - 1]}"
        if (TWO_PART_TLDS.contains(lastTwo) && parts.size >= 3) {
            return "${parts[parts.size - 3]}.$lastTwo"
        }

        return lastTwo
    }
}
