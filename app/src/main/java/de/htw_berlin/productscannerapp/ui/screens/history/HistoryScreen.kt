package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.htw_berlin.productscannerapp.ui.components.EmptyState

@Composable
fun HistoryScreen(
    innerPadding: PaddingValues,
    onOpenItem: (String) -> Unit
) {
    // Fake list for now; backend will replace later
    var history by remember {
        mutableStateOf(
            listOf(
                HistoryItemUi("4006381333931", "Chocolate Bar", "Sample Brand", "2 min ago"),
                HistoryItemUi("1234567890123", "Pasta", "Brand X", "Yesterday"),
                HistoryItemUi("978020137962", "Unknown Product", null, "Last week")
            )
        )
    }

    if (history.isEmpty()) {
        EmptyState(
            title = "No scans yet",
            subtitle = "Scan a product to see it here.",
            modifier = Modifier.padding(innerPadding)
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Recent scans", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { history = emptyList() }) {
                Text("Clear")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(history, key = { it.barcode }) { item ->
                HistoryItemCard(item = item, onClick = onOpenItem)
            }
        }
    }
}
