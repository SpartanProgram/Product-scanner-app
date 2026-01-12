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

fun FoodCategory.defaultLabel(): String = when (this) {
    FoodCategory.VEGAN -> "Vegan"
    FoodCategory.VEGETARIAN -> "Vegetarian"
    FoodCategory.HALAL -> "Halal"
    FoodCategory.NON_HALAL -> "Not allowed"
    FoodCategory.NOT_VEGAN -> "Not vegan"
    FoodCategory.NOT_VEGETARIAN -> "Contains meat"
    FoodCategory.UNKNOWN -> "Info"
}

