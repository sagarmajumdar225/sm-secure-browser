package com.smsecure.browser.privacy

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smsecure.browser.data.SiteExceptionDao
import com.smsecure.browser.data.SiteExceptionEntity
import com.smsecure.browser.util.PublicSuffixList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerSiteControlBottomSheet(
    url: String,
    blockedCount: Int,
    exceptionDao: SiteExceptionDao,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val domain = remember(url) { PublicSuffixList.getRegistrableDomain(url) }

    var allowCookies by remember { mutableStateOf(false) }
    var allowStorage by remember { mutableStateOf(false) }
    var allowScripts by remember { mutableStateOf(true) }

    LaunchedEffect(domain) {
        val existing = exceptionDao.getForDomain(domain)
        if (existing != null) {
            allowCookies = existing.allowCookies
            allowStorage = existing.allowStorage
            allowScripts = existing.allowScripts
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Privacy Shield",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = domain.ifBlank { "Current Site" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$blockedCount",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Trackers & fingerprinting scripts blocked on this page",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Site Permissions & Exceptions",
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow(
                label = "Allow Third-Party Cookies",
                checked = allowCookies,
                onCheckedChange = { checked ->
                    allowCookies = checked
                    saveException(scope, exceptionDao, domain, allowCookies, allowStorage, allowScripts)
                }
            )

            ToggleRow(
                label = "Allow Local Storage Access",
                checked = allowStorage,
                onCheckedChange = { checked ->
                    allowStorage = checked
                    saveException(scope, exceptionDao, domain, allowCookies, allowStorage, allowScripts)
                }
            )

            ToggleRow(
                label = "Enable JavaScript Execution",
                checked = allowScripts,
                onCheckedChange = { checked ->
                    allowScripts = checked
                    saveException(scope, exceptionDao, domain, allowCookies, allowStorage, allowScripts)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun saveException(
    scope: CoroutineScope,
    dao: SiteExceptionDao,
    domain: String,
    cookies: Boolean,
    storage: Boolean,
    scripts: Boolean
) {
    scope.launch(Dispatchers.IO) {
        dao.insert(
            SiteExceptionEntity(
                domain = domain,
                allowCookies = cookies,
                allowStorage = storage,
                allowScripts = scripts
            )
        )
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
