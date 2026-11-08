package com.smsecure.browser.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smsecure.browser.browser.BrowserSession

@Composable
fun TabTrayScreen(
    tabs: List<BrowserSession>,
    activeTabId: String?,
    onTabSelected: (String) -> Unit,
    onTabClosed: (String) -> Unit,
    onNewTab: () -> Unit,
    onBurnSession: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(onNewTab = onNewTab, onBurnSession = onBurnSession)
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { session ->
                TabCard(
                    session = session,
                    isActive = session.id == activeTabId,
                    onClick = { onTabSelected(session.id) },
                    onClose = { onTabClosed(session.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    onNewTab: () -> Unit,
    onBurnSession: () -> Unit
) {
    androidx.compose.material3.TopAppBar(
        title = { Text("Tabs") },
        actions = {
            IconButton(onClick = onBurnSession) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Burn Session",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            IconButton(onClick = onNewTab) {
                Icon(Icons.Default.Add, contentDescription = "New Tab")
            }
        }
    )
}

@Composable
private fun TabCard(
    session: BrowserSession,
    isActive: Boolean,
    onClick: () -> Unit,
    onClose: () -> Unit
) {
    val title by session.title.collectAsState()
    val url by session.url.collectAsState()
    val isSuspended by session.isSuspended.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer 
                             else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.ifEmpty { "New Tab" },
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close Tab",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = url.ifEmpty { "about:blank" },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // RAM usage badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSuspended) MaterialTheme.colorScheme.surface
                        else MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = session.estimatedRamUsageMb,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
