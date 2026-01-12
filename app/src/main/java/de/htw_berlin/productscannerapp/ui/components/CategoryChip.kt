package de.htw_berlin.productscannerapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
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
    val (colors, icon) = when (tag.category) {
        FoodCategory.HALAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) to Icons.Outlined.Verified

        // ❌ not allowed
        FoodCategory.NON_HALAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
        ) to Icons.Outlined.Block

        // ❌ not allowed (fails vegan)
        FoodCategory.NOT_VEGAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
        ) to Icons.Outlined.Block

        // 🥩 contains meat (fails vegetarian)
        FoodCategory.NOT_VEGETARIAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
        ) to Icons.Outlined.Restaurant

        // 🌱 vegan / vegetarian (positive)
        FoodCategory.VEGAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ) to Icons.Outlined.Spa

        FoodCategory.VEGETARIAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) to Icons.Outlined.Spa

        // ℹ️ neutral info
        FoodCategory.UNKNOWN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) to Icons.Outlined.Info
    }

    AssistChip(
        onClick = { /* no-op */ },
        label = { Text(tag.label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        colors = colors,
        modifier = modifier.padding(end = 8.dp)
    )
}
