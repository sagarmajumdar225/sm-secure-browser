package com.smsecure.browser.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.smsecure.browser.browser.TabManager
import com.smsecure.browser.settings.ThemeManager
import com.smsecure.browser.settings.ThemeMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var tabManager: TabManager
    private lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enforce FLAG_SECURE to prevent background screenshots and recent task thumbnail leaks
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        themeManager = ThemeManager(applicationContext)
        val initialTor = runBlocking { themeManager.torEnabled.first() }
        val initialAdBlock = runBlocking { themeManager.adBlockingEnabled.first() }

        tabManager = TabManager(applicationContext, adBlockingEnabled = initialAdBlock, torEnabled = initialTor)

        if (tabManager.sessions.value.isEmpty()) {
            tabManager.createNewTab("https://duckduckgo.com")
        }

        enableEdgeToEdge()
        setContent {
            val themeModeState by themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            var currentTheme by remember(themeModeState) { mutableStateOf(themeModeState) }
            val scope = rememberCoroutineScope()

            val useDarkTheme = when (currentTheme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val colorScheme = if (useDarkTheme) darkColorScheme() else lightColorScheme()

            MaterialTheme(colorScheme = colorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrowserScreen(
                        tabManager = tabManager,
                        currentTheme = currentTheme,
                        onThemeChanged = { newTheme ->
                            currentTheme = newTheme
                            scope.launch { themeManager.setThemeMode(newTheme) }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            tabManager.closeAllTabs()
        }
    }
}
