package de.htw_berlin.productscannerapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChip(
    tag: CategoryTag,
    modifier: Modifier = Modifier
) {
    val colors = when (tag.category) {
        FoodCategory.HALAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        FoodCategory.NON_HALAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer
        )
        FoodCategory.VEGETARIAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
        FoodCategory.VEGAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        FoodCategory.UNKNOWN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    AssistChip(
        onClick = { /* no-op for now */ },
        label = { Text(tag.label) },
        colors = colors,
        modifier = modifier.padding(end = 8.dp)
    )
}
