package de.htw_berlin.productscannerapp.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import de.htw_berlin.productscannerapp.ui.components.CategoryChip
import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory

data class ProductDetailUiState(
    val name: String,
    val brand: String?,
    val barcode: String,
    val categories: List<CategoryTag>,
    val reasons: List<String>,
    val ingredients: String?,
)

@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    barcode: String,
) {
    // Fake UI State for now (backend can replace later)
    val state = ProductDetailUiState(
        name = "Sample Product",
        brand = "Brand Name",
        barcode = barcode,
        categories = listOf(
            CategoryTag(FoodCategory.VEGETARIAN, "Vegetarian"),
            CategoryTag(FoodCategory.HALAL, "Halal"),
            CategoryTag(FoodCategory.UNKNOWN, "Unknown")
        ),
        reasons = listOf(
            "No clear certification found → Halal status not guaranteed",
            "Contains milk powder → not vegan",
            "No meat ingredients listed → vegetarian likely"
        ),
        ingredients = "Sugar, Wheat Flour, Milk Powder, Cocoa Butter, Emulsifier (Soy Lecithin)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = state.name,
                style = MaterialTheme.typography.headlineSmall
            )
            state.brand?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Barcode: ${state.barcode}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Categories",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.categories) { tag ->
                        CategoryChip(tag = tag)
                    }
                }
            }
        }

        state.ingredients?.let { ing ->
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ingredients",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = ing,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Why this result?",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                if (state.reasons.isEmpty()) {
                    Text(
                        text = "No explanation available.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    // Using LazyColumn already, so we just list items here
                    state.reasons.forEach { reason ->
                        Text(
                            text = "• $reason",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}
