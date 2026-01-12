package de.htw_berlin.productscannerapp.ui.components

enum class FoodCategory {
    VEGAN,
    VEGETARIAN,
    HALAL,
    NON_HALAL,
    NOT_VEGAN,
    NOT_VEGETARIAN,
    UNKNOWN,
}

data class CategoryTag(
    val category: FoodCategory,
    val label: String = category.defaultLabel()
)
