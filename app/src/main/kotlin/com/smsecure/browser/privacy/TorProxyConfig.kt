package com.smsecure.browser.privacy

object TorProxyConfig {
    /**
     * Returns Gecko process arguments to route all traffic and remote DNS queries through a local SOCKS5 proxy (e.g. Orbot on localhost:9050).
     */
    fun getProxyArguments(enabled: Boolean): Array<String> {
        if (!enabled) return emptyArray()
        return arrayOf(
            // Pref: network.proxy.type=1 - Manual proxy configuration
            "-pref", "network.proxy.type=1",
            // Pref: network.proxy.socks=127.0.0.1 - SOCKS proxy host
            "-pref", "network.proxy.socks=127.0.0.1",
            // Pref: network.proxy.socks_port=9050 - SOCKS proxy port (Orbot default)
            "-pref", "network.proxy.socks_port=9050",
            // Pref: network.proxy.socks_version=5 - SOCKS5 protocol
            "-pref", "network.proxy.socks_version=5",
            // Pref: network.proxy.socks_remote_dns=true - Route DNS queries over SOCKS5 proxy to prevent DNS leaks
            "-pref", "network.proxy.socks_remote_dns=true"
        )
    }
}
