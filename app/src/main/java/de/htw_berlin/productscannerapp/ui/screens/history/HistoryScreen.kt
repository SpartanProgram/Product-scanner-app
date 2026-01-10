package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.htw_berlin.productscannerapp.ui.components.EmptyState
import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory


@Composable
fun HistoryScreen(
    innerPadding: PaddingValues,
    onOpenItem: (String) -> Unit
) {
    // Fake list for now; backend will replace later
    var history = listOf(
        HistoryItemUi(
            barcode = "4006381333931",
            name = "Chocolate Bar",
            brand = "Sample Brand",
            timestampLabel = "2 min ago",
            categories = listOf(
                CategoryTag(FoodCategory.VEGETARIAN, "Vegetarian"),
                CategoryTag(FoodCategory.NON_HALAL, "Non-Halal")
            )
        ),
        HistoryItemUi(
            barcode = "1234567890123",
            name = "Pasta",
            brand = "Brand X",
            timestampLabel = "Yesterday",
            categories = listOf(
                CategoryTag(FoodCategory.VEGAN, "Vegan"),
                CategoryTag(FoodCategory.HALAL, "Halal")
            )
        ),
        HistoryItemUi(
            barcode = "978020137962",
            name = "Unknown Product",
            brand = null,
            timestampLabel = "Last week",
            categories = listOf(
                CategoryTag(FoodCategory.UNKNOWN, "Unknown")
            )
        )
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
