package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.htw_berlin.productscannerapp.ui.components.EmptyState

@Composable
fun HistoryScreen(
    innerPadding: PaddingValues,
    onOpenItem: (String) -> Unit
) {
    // Fake data for now (backend will replace later)
    val history = listOf(
        HistoryItemUi("4006381333931", "Chocolate Bar", "Sample Brand", "2 min ago"),
        HistoryItemUi("1234567890123", "Pasta", "Brand X", "Yesterday"),
        HistoryItemUi("978020137962", "Unknown Product", null, "Last week")
    )

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
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Recent scans",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))

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
