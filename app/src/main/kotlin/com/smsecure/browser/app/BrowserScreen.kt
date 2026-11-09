package com.smsecure.browser.app

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.smsecure.browser.browser.BrowserSession
import com.smsecure.browser.browser.TabManager
import com.smsecure.browser.data.AppDatabase
import com.smsecure.browser.data.BookmarkEntity
import com.smsecure.browser.privacy.ContentBlockingMode
import com.smsecure.browser.privacy.PerSiteControlBottomSheet
import com.smsecure.browser.settings.SettingsScreen
import com.smsecure.browser.settings.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class ScreenState {
    BROWSER,
    HISTORY,
    BOOKMARKS,
    SETTINGS
}

@Composable
fun BrowserScreen(
    tabManager: TabManager,
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var currentProtectionMode by remember { mutableStateOf(ContentBlockingMode.STRICT) }

    val sessions by tabManager.sessions.collectAsState()
    val activeSessionId by tabManager.activeSessionId.collectAsState()
    var showTabTray by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf(ScreenState.BROWSER) }

    val activeSession = remember(sessions, activeSessionId) {
        sessions.find { it.id == activeSessionId }
    }

    val canGoBackState = activeSession?.canGoBack?.collectAsState()?.value == true

    // System Back Button Handling (GeckoSession history navigation or Tab Tray dismissal)
    BackHandler(enabled = currentScreen == ScreenState.BROWSER) {
        if (showTabTray) {
            showTabTray = false
        } else if (canGoBackState && activeSession != null) {
            activeSession.goBack()
        } else {
            (context as? Activity)?.finish()
        }
    }

    when (currentScreen) {
        ScreenState.HISTORY -> {
            HistoryScreen(
                historyDao = db.historyDao(),
                onUrlSelected = { url ->
                    activeSession?.loadUrl(url)
                    currentScreen = ScreenState.BROWSER
                },
                onBack = { currentScreen = ScreenState.BROWSER }
            )
        }
        ScreenState.BOOKMARKS -> {
            BookmarkScreen(
                bookmarkDao = db.bookmarkDao(),
                onUrlSelected = { url ->
                    activeSession?.loadUrl(url)
                    currentScreen = ScreenState.BROWSER
                },
                onBack = { currentScreen = ScreenState.BROWSER }
            )
        }
        ScreenState.SETTINGS -> {
            SettingsScreen(
                currentMode = currentProtectionMode,
                currentTheme = currentTheme,
                onModeChanged = { newMode -> currentProtectionMode = newMode },
                onThemeChanged = onThemeChanged,
                onBack = { currentScreen = ScreenState.BROWSER }
            )
        }
        ScreenState.BROWSER -> {
            Scaffold(
                modifier = modifier.fillMaxSize(),
                topBar = {
                    if (!showTabTray && activeSession != null) {
                        BrowserTopBar(
                            session = activeSession,
                            tabCount = sessions.size,
                            db = db,
                            onOpenTabTray = { showTabTray = true },
                            onOpenHistory = { currentScreen = ScreenState.HISTORY },
                            onOpenBookmarks = { currentScreen = ScreenState.BOOKMARKS },
                            onOpenSettings = { currentScreen = ScreenState.SETTINGS },
                            onBurnSession = {
                                tabManager.burnSession()
                                Toast.makeText(context, "Session burned! Cookies & storage cleared.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (activeSession != null && !showTabTray) {
                        GeckoViewContainer(
                            session = activeSession.session,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    AnimatedVisibility(
                        visible = showTabTray,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TabTrayScreen(
                            tabs = sessions,
                            activeTabId = activeSessionId,
                            onTabSelected = { id ->
                                tabManager.switchTab(id)
                                showTabTray = false
                            },
                            onTabClosed = { id ->
                                tabManager.closeTab(id)
                            },
                            onNewTab = {
                                tabManager.createNewTab()
                                showTabTray = false
                            },
                            onBurnSession = {
                                tabManager.burnSession()
                                showTabTray = false
                                Toast.makeText(context, "Session burned! Cookies & storage cleared.", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrowserTopBar(
    session: BrowserSession,
    tabCount: Int,
    db: AppDatabase,
    onOpenTabTray: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSettings: () -> Unit,
    onBurnSession: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val url by session.url.collectAsState()
    val title by session.title.collectAsState()
    val isLoading by session.isLoading.collectAsState()
    val progress by session.progress.collectAsState()
    val canGoBack by session.canGoBack.collectAsState()
    val canGoForward by session.canGoForward.collectAsState()
    val blockedCount by session.blockedCount.collectAsState()

    val isBookmarked by db.bookmarkDao().isBookmarked(url).collectAsState(initial = false)
    var menuExpanded by remember { mutableStateOf(false) }
    var showShieldBottomSheet by remember { mutableStateOf(false) }

    var textFieldValue by remember(url) { mutableStateOf(url) }
    var isUrlFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    if (showShieldBottomSheet) {
        PerSiteControlBottomSheet(
            url = url,
            blockedCount = blockedCount,
            exceptionDao = db.siteExceptionDao(),
            onDismiss = { showShieldBottomSheet = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hide back button when typing in URL bar to give maximum width expansion
            if (!isUrlFocused) {
                IconButton(
                    onClick = { session.goBack() },
                    enabled = canGoBack
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }

            // Expandable URL/Search Bar with focus state tracking
            TextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .onFocusChanged { state ->
                        isUrlFocused = state.isFocused
                        if (state.isFocused && textFieldValue.isEmpty()) {
                            textFieldValue = url
                        }
                    },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        session.loadUrl(textFieldValue)
                        focusManager.clearFocus()
                    }
                ),
                placeholder = { Text("Search or type URL") },
                leadingIcon = {
                    IconButton(onClick = { showShieldBottomSheet = true }) {
                        BadgedBox(
                            badge = {
                                if (blockedCount > 0) {
                                    Badge { Text(blockedCount.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Privacy Shield",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (url.isNotBlank() && !url.startsWith("about:")) {
                        IconButton(onClick = {
                            scope.launch(Dispatchers.IO) {
                                if (isBookmarked) {
                                    db.bookmarkDao().deleteByUrl(url)
                                } else {
                                    db.bookmarkDao().insert(
                                        BookmarkEntity(
                                            url = url,
                                            title = title.ifBlank { url }
                                        )
                                    )
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = if (isBookmarked) "Remove Bookmark" else "Bookmark Page",
                                tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.width(4.dp))

            BadgedBox(
                badge = {
                    Badge { Text(tabCount.toString()) }
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                IconButton(onClick = onOpenTabTray) {
                    Icon(Icons.Default.Menu, contentDescription = "Tab Tray")
                }
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (isLoading) "Stop" else "Reload") },
                        onClick = {
                            menuExpanded = false
                            if (isLoading) session.stop() else session.reload()
                        }
                    )
                    if (canGoForward) {
                        DropdownMenuItem(
                            text = { Text("Forward") },
                            onClick = {
                                menuExpanded = false
                                session.goForward()
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Burn Session") },
                        onClick = {
                            menuExpanded = false
                            onBurnSession()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Bookmarks") },
                        onClick = {
                            menuExpanded = false
                            onOpenBookmarks()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("History") },
                        onClick = {
                            menuExpanded = false
                            onOpenHistory()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            menuExpanded = false
                            onOpenSettings()
                        }
                    )
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
