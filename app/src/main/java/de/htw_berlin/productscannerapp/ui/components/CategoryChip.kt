package de.htw_berlin.productscannerapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CategoryChip(
    tag: CategoryTag,
    modifier: Modifier = Modifier
) {
    val colors = when (tag.category) {
        FoodCategory.NON_HALAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
        )

        FoodCategory.NOT_VEGETARIAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )

        FoodCategory.UNKNOWN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // “positive” categories
        FoodCategory.VEGAN, FoodCategory.VEGETARIAN, FoodCategory.HALAL -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        FoodCategory.NOT_VEGAN -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val icon = when (tag.category) {
        FoodCategory.NON_HALAL -> Icons.Outlined.Block          // ❌ Not allowed
        FoodCategory.NOT_VEGETARIAN -> Icons.Outlined.Restaurant // 🥩 Contains meat (closest built-in)
        FoodCategory.UNKNOWN -> Icons.Outlined.Info            // ℹ️ Info

        FoodCategory.VEGAN -> Icons.Outlined.Spa               // 🌿 Vegan vibe
        FoodCategory.VEGETARIAN -> Icons.Outlined.Spa          // 🌿 Vegetarian vibe
        FoodCategory.HALAL -> Icons.Outlined.Restaurant        // 🍽 Halal (ok default)

        FoodCategory.NOT_VEGAN -> Icons.Outlined.Block         // “Not vegan” feels like a block
    }

    AssistChip(
        onClick = { /* no-op */ },
        label = { Text(tag.label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        colors = colors,
        modifier = modifier
    )
}
