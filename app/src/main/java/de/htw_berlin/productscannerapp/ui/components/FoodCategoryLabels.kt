package de.htw_berlin.productscannerapp.ui.components

fun FoodCategory.defaultLabel(): String = when (this) {
    FoodCategory.VEGAN -> "Vegan"
    FoodCategory.VEGETARIAN -> "Vegetarian"
    FoodCategory.HALAL -> "Halal"
    FoodCategory.NON_HALAL -> "Non-halal"
    FoodCategory.NOT_VEGAN -> "Not vegan"
    FoodCategory.NOT_VEGETARIAN -> "Not vegetarian"
    FoodCategory.UNKNOWN -> "Info"
}
