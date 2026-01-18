package de.htw_berlin.productscannerapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Eco
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChip(
    tag: CategoryTag,
    modifier: Modifier = Modifier
) {
    val isNegative = when (tag.category) {
        FoodCategory.NON_HALAL,
        FoodCategory.NOT_VEGETARIAN,
        FoodCategory.NOT_VEGAN -> true
        else -> false
    }

    val isPositive = when (tag.category) {
        FoodCategory.HALAL,
        FoodCategory.VEGETARIAN,
        FoodCategory.VEGAN -> true
        else -> false
    }

    val colors = when {
        isNegative -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
        )

        isPositive -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        else -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val icon: ImageVector = when (tag.category) {
        FoodCategory.NON_HALAL -> Icons.Outlined.Block
        FoodCategory.NOT_VEGETARIAN -> Icons.Outlined.Restaurant
        FoodCategory.NOT_VEGAN -> Icons.Outlined.Block

        FoodCategory.HALAL -> Icons.Outlined.Verified
        FoodCategory.VEGETARIAN -> Icons.Outlined.Eco
        FoodCategory.VEGAN -> Icons.Outlined.Spa

        FoodCategory.UNKNOWN -> Icons.Outlined.Info
    }

    AssistChip(
        onClick = { /* no-op */ },
        label = { Text(tag.label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        colors = colors,
        modifier = modifier.padding(end = 8.dp)
    )
}
