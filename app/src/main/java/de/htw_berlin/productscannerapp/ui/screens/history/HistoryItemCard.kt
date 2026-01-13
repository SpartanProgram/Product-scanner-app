package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.htw_berlin.productscannerapp.ui.components.CategoryChip

@Composable
fun HistoryItemCard(
    item: HistoryItemUi,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(item.barcode) }
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Text(item.name, style = MaterialTheme.typography.titleMedium)

            item.brand?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "Barcode: ${item.barcode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // quantity + nutri-score (only show if at least one exists)
            if (!item.quantity.isNullOrBlank() || !item.nutriScoreGrade.isNullOrBlank()) {
                val parts = buildList {
                    item.quantity?.takeIf { it.isNotBlank() }?.let { add(it) }
                    item.nutriScoreGrade?.takeIf { it.isNotBlank() }?.let { grade ->
                        add("Nutri-Score ${grade.uppercase()}")
                    }
                }

                Text(
                    text = parts.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (item.categories.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(item.categories) { tag ->
                        CategoryChip(tag = tag)
                    }
                }
            }
        }
    }
}
