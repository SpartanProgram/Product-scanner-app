package de.htw_berlin.productscannerapp.ui.components

enum class FoodCategory {
    HALAL, NON_HALAL, VEGETARIAN, VEGAN, UNKNOWN
}

data class CategoryTag(
    val category: FoodCategory,
    val label: String
)
