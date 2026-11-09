package com.smsecure.browser.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.smsecure.browser.browser.GeckoRuntimeManager
import com.smsecure.browser.privacy.ContentBlockingMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentMode: ContentBlockingMode,
    currentTheme: ThemeMode,
    onModeChanged: (ContentBlockingMode) -> Unit,
    onThemeChanged: (ThemeMode) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeManager = remember { ThemeManager(context) }
    val torEnabled by themeManager.torEnabled.collectAsState(initial = false)
    val adBlockingEnabled by themeManager.adBlockingEnabled.collectAsState(initial = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Appearance & Theme",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))

            ThemeOption(
                title = "System Default",
                selected = currentTheme == ThemeMode.SYSTEM,
                onClick = {
                    onThemeChanged(ThemeMode.SYSTEM)
                    scope.launch { themeManager.setThemeMode(ThemeMode.SYSTEM) }
                }
            )
            ThemeOption(
                title = "Light Theme",
                selected = currentTheme == ThemeMode.LIGHT,
                onClick = {
                    onThemeChanged(ThemeMode.LIGHT)
                    scope.launch { themeManager.setThemeMode(ThemeMode.LIGHT) }
                }
            )
            ThemeOption(
                title = "Dark Theme",
                selected = currentTheme == ThemeMode.DARK,
                onClick = {
                    onThemeChanged(ThemeMode.DARK)
                    scope.launch { themeManager.setThemeMode(ThemeMode.DARK) }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Ad-Blocking & Anti-Tracking",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Enforce GeckoView ContentBlocking rules to block ads, analytics trackers, social scripts, and cryptominers.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Enable Ad-Blocking", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Active anti-tracking & ad filtering", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = adBlockingEnabled,
                    onCheckedChange = { checked ->
                        scope.launch {
                            themeManager.setAdBlockingEnabled(checked)
                            GeckoRuntimeManager.updateContentBlockingMode(checked)
                        }
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Network & Proxy",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Route all traffic and remote DNS through local SOCKS5 proxy (Orbot / Tor on localhost:9050).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Enable Tor / Orbot Proxy Mode", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Requires Orbot app running locally on port 9050", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = torEnabled,
                    onCheckedChange = { checked ->
                        scope.launch {
                            themeManager.setTorEnabled(checked)
                        }
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Global Protection Mode",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select the default privacy protection level for all sites.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModeOption(
                title = "Strict (Default)",
                description = "Blocks all known trackers, fingerprinting, and enforces dFPI cookie isolation.",
                selected = currentMode == ContentBlockingMode.STRICT,
                onClick = {
                    onModeChanged(ContentBlockingMode.STRICT)
                    GeckoRuntimeManager.updateContentBlockingMode(true)
                }
            )

            ModeOption(
                title = "Standard",
                description = "Blocks standard tracking scripts and isolates third-party cookies.",
                selected = currentMode == ContentBlockingMode.STANDARD,
                onClick = {
                    onModeChanged(ContentBlockingMode.STANDARD)
                    GeckoRuntimeManager.updateContentBlockingMode(true)
                }
            )

            ModeOption(
                title = "Off",
                description = "Disables enhanced tracking protection completely (not recommended).",
                selected = currentMode == ContentBlockingMode.OFF,
                onClick = {
                    onModeChanged(ContentBlockingMode.OFF)
                    GeckoRuntimeManager.updateContentBlockingMode(false)
                }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ModeOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
