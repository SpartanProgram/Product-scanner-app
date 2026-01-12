package de.htw_berlin.productscannerapp.ui.components

fun FoodCategory.defaultLabel(): String = when (this) {
    FoodCategory.NON_HALAL -> "Not allowed"        // ❌
    FoodCategory.NOT_VEGETARIAN -> "Contains meat" // 🥩
    FoodCategory.NOT_VEGAN -> "Not vegan"          // 🚫
    FoodCategory.VEGETARIAN -> "Vegetarian"        // 🥗
    FoodCategory.VEGAN -> "Vegan"                  // 🌱
    FoodCategory.HALAL -> "Allowed"                // ✅ (optional)
    FoodCategory.UNKNOWN -> "Info"                 // ℹ️
}
