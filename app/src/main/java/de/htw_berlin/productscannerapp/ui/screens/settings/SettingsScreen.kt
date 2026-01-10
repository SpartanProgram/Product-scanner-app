package de.htw_berlin.productscannerapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(innerPadding: PaddingValues) {
    var offlineMode by remember { mutableStateOf(false) }
    var rememberHistory by remember { mutableStateOf(true) }
    var dataSource by remember { mutableStateOf("Open Food Facts (online)") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleMedium)

        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("General", style = MaterialTheme.typography.titleSmall)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text("Offline mode")
                        Text(
                            "Use cached results only (no network).",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(checked = offlineMode, onCheckedChange = { offlineMode = it })
                }

                Divider()

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text("Save scan history")
                        Text(
                            "Keep a list of scanned products.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(checked = rememberHistory, onCheckedChange = { rememberHistory = it })
                }
            }
        }

        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Data source", style = MaterialTheme.typography.titleSmall)

                // placeholder dropdown behavior: cycle on click
                FilledTonalButton(
                    onClick = {
                        dataSource = if (dataSource.startsWith("Open")) "Mock data (offline demo)"
                        else "Open Food Facts (online)"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(dataSource)
                }

                Text(
                    "Backend can later replace this with a real selector.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
