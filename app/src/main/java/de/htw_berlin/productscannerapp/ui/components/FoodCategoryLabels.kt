// ui/components/FoodCategoryLabels.kt
package de.htw_berlin.productscannerapp.ui.components

fun FoodCategory.defaultLabel(): String = when (this) {
    FoodCategory.NON_HALAL -> "Not allowed"       // ❌ block vibe
    FoodCategory.NOT_VEGETARIAN -> "Contains meat"// 🥩 vibe
    FoodCategory.UNKNOWN -> "Info"               // ℹ️ vibe

    FoodCategory.HALAL -> "Halal"
    FoodCategory.VEGAN -> "Vegan"
    FoodCategory.VEGETARIAN -> "Vegetarian"
    FoodCategory.NOT_VEGAN -> "Not vegan"
}
