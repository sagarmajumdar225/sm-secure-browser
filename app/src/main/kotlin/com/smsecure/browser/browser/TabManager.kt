package com.smsecure.browser.browser

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.StorageController

class TabManager(
    private val context: Context,
    private val adBlockingEnabled: Boolean = true,
    private val torEnabled: Boolean = false
) {
    private val _sessions = MutableStateFlow<List<BrowserSession>>(emptyList())
    val sessions: StateFlow<List<BrowserSession>> = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId.asStateFlow()

    private val SUSPENSION_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes

    fun createNewTab(url: String = ""): BrowserSession {
        checkAndSuspendInactiveTabs()

        val geckoSession = GeckoSession()
        geckoSession.open(GeckoRuntimeManager.getRuntime(context, adBlockingEnabled = adBlockingEnabled, torEnabled = torEnabled))

        val browserSession = BrowserSession(session = geckoSession)
        browserSession.setupDelegates(context)

        if (url.isNotEmpty()) {
            browserSession.loadUrl(url)
        }

        val currentList = _sessions.value.toMutableList()
        currentList.add(browserSession)
        _sessions.value = currentList
        _activeSessionId.value = browserSession.id

        return browserSession
    }

    fun switchTab(id: String) {
        val previousActiveId = _activeSessionId.value
        if (previousActiveId != null && previousActiveId != id) {
            _sessions.value.find { it.id == previousActiveId }?.lastActiveTimeMs = System.currentTimeMillis()
        }

        val target = _sessions.value.find { it.id == id }
        if (target != null) {
            if (target.isSuspended.value) {
                target.restore(context)
            }
            target.lastActiveTimeMs = System.currentTimeMillis()
            _activeSessionId.value = id
        }

        checkAndSuspendInactiveTabs()
    }

    fun closeTab(id: String) {
        val currentList = _sessions.value.toMutableList()
        val sessionToRemove = currentList.find { it.id == id }
        sessionToRemove?.session?.close()
        currentList.removeIf { it.id == id }

        _sessions.value = currentList

        if (_activeSessionId.value == id) {
            _activeSessionId.value = currentList.lastOrNull()?.id
        }

        if (currentList.isEmpty()) {
            createNewTab()
        }
    }

    fun checkAndSuspendInactiveTabs() {
        val activeId = _activeSessionId.value
        val now = System.currentTimeMillis()

        _sessions.value.forEach { session ->
            if (session.id != activeId && !session.isSuspended.value) {
                if (now - session.lastActiveTimeMs >= SUSPENSION_TIMEOUT_MS) {
                    session.suspend()
                }
            }
        }
    }

    fun burnSession() {
        // API: StorageController.clearData(ClearFlags.ALL) - Wipes all cookies, storage, cache, and site data
        GeckoRuntimeManager.getRuntime(context, adBlockingEnabled = adBlockingEnabled, torEnabled = torEnabled).storageController.clearData(StorageController.ClearFlags.ALL)

        _sessions.value.forEach { it.session.close() }
        _sessions.value = emptyList()
        _activeSessionId.value = null

        createNewTab("https://duckduckgo.com")
    }

    fun getActiveSession(): BrowserSession? {
        val currentId = _activeSessionId.value ?: return null
        return _sessions.value.find { it.id == currentId }
    }

    fun closeAllTabs() {
        _sessions.value.forEach { it.session.close() }
        _sessions.value = emptyList()
        _activeSessionId.value = null
    }

    fun saveStateToSerialized(): List<Pair<String, String?>> {
        return _sessions.value.map { session ->
            Pair(session.url.value, session.savedState?.toString())
        }
    }
}
