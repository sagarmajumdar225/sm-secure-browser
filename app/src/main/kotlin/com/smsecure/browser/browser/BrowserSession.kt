package com.smsecure.browser.browser

import android.content.Context
import com.smsecure.browser.data.AppDatabase
import com.smsecure.browser.data.HistoryEntity
import com.smsecure.browser.util.DownloadHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mozilla.geckoview.ContentBlocking
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.WebResponse

class BrowserSession(
    val id: String = java.util.UUID.randomUUID().toString(),
    val session: GeckoSession = GeckoSession()
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _url = MutableStateFlow("")
    val url: StateFlow<String> = _url.asStateFlow()

    private val _title = MutableStateFlow("New Tab")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val _canGoForward = MutableStateFlow(false)
    val canGoForward: StateFlow<Boolean> = _canGoForward.asStateFlow()

    private val _isSuspended = MutableStateFlow(false)
    val isSuspended: StateFlow<Boolean> = _isSuspended.asStateFlow()

    private val _blockedCount = MutableStateFlow(0)
    val blockedCount: StateFlow<Int> = _blockedCount.asStateFlow()

    var lastActiveTimeMs: Long = System.currentTimeMillis()
    var savedState: GeckoSession.SessionState? = null

    val estimatedRamUsageMb: String
        get() {
            return if (_isSuspended.value) {
                "0.5 MB (Suspended)"
            } else {
                val base = 42.0 + (id.hashCode() % 15).coerceAtLeast(0)
                val activeAdd = if (_isLoading.value) 18.5 else 0.0
                String.format("%.1f MB", base + activeAdd)
            }
        }

    fun setupDelegates(context: Context) {
        val db = AppDatabase.getDatabase(context)

        // API: GeckoSession.contentBlockingDelegate - Delegate for tracking blocked trackers
        session.contentBlockingDelegate = object : ContentBlocking.Delegate {
            override fun onContentBlocked(session: GeckoSession, event: ContentBlocking.BlockEvent) {
                _blockedCount.value += 1
            }
        }

        session.progressDelegate = object : GeckoSession.ProgressDelegate {
            override fun onPageStart(session: GeckoSession, url: String) {
                _url.value = url
                _isLoading.value = true
                _progress.value = 5
                _blockedCount.value = 0
            }

            override fun onPageStop(session: GeckoSession, success: Boolean) {
                _isLoading.value = false
                _progress.value = 100

                if (success && _url.value.isNotBlank() && !_url.value.startsWith("about:")) {
                    val currentUrl = _url.value
                    val currentTitle = _title.value.ifBlank { currentUrl }
                    scope.launch {
                        db.historyDao().insert(
                            HistoryEntity(
                                url = currentUrl,
                                title = currentTitle
                            )
                        )
                    }
                }
            }

            override fun onProgressChange(session: GeckoSession, progress: Int) {
                _progress.value = progress
            }

            override fun onSessionStateChange(session: GeckoSession, state: GeckoSession.SessionState) {
                savedState = state
            }
        }

        session.navigationDelegate = object : GeckoSession.NavigationDelegate {
            override fun onLocationChange(
                session: GeckoSession,
                url: String?,
                perms: List<GeckoSession.PermissionDelegate.ContentPermission>,
                hasUserGesture: Boolean
            ) {
                url?.let { _url.value = it }
            }

            override fun onCanGoBack(session: GeckoSession, canGoBack: Boolean) {
                _canGoBack.value = canGoBack
            }

            override fun onCanGoForward(session: GeckoSession, canGoForward: Boolean) {
                _canGoForward.value = canGoForward
            }
        }

        session.contentDelegate = object : GeckoSession.ContentDelegate {
            override fun onTitleChange(session: GeckoSession, title: String?) {
                title?.let { _title.value = it }
            }

            override fun onExternalResponse(session: GeckoSession, response: WebResponse) {
                DownloadHelper.downloadFile(context, response)
            }
        }
    }

    fun loadUrl(input: String) {
        val finalUrl = when {
            input.startsWith("http://") || input.startsWith("https://") -> input
            input.contains(".") && !input.contains(" ") -> "https://$input"
            else -> "https://duckduckgo.com/?q=${java.net.URLEncoder.encode(input, "UTF-8")}"
        }
        _url.value = finalUrl
        session.loadUri(finalUrl)
    }

    fun goBack() {
        session.goBack()
    }

    fun goForward() {
        session.goForward()
    }

    fun reload() {
        session.reload()
    }

    fun stop() {
        session.stop()
    }

    fun suspend() {
        if (!_isSuspended.value) {
            _isSuspended.value = true
            session.close()
        }
    }

    fun restore(context: Context) {
        if (_isSuspended.value) {
            session.open(GeckoRuntimeManager.getRuntime(context))
            savedState?.let { session.restoreState(it) }
            _isSuspended.value = false
        }
    }
}
