package de.htw_berlin.productscannerapp.ui.components

fun triadCategories(all: List<CategoryTag>): List<CategoryTag> {
    val set = all.map { it.category }.toSet()

    val halal = when {
        FoodCategory.NON_HALAL in set -> CategoryTag(FoodCategory.NON_HALAL, FoodCategory.NON_HALAL.defaultLabel())
        FoodCategory.HALAL in set -> CategoryTag(FoodCategory.HALAL, FoodCategory.HALAL.defaultLabel())
        else -> CategoryTag(FoodCategory.UNKNOWN, "Halal: unknown")
    }

    val vegetarian = when {
        FoodCategory.NOT_VEGETARIAN in set -> CategoryTag(FoodCategory.NOT_VEGETARIAN, FoodCategory.NOT_VEGETARIAN.defaultLabel())
        FoodCategory.VEGETARIAN in set -> CategoryTag(FoodCategory.VEGETARIAN, FoodCategory.VEGETARIAN.defaultLabel())
        else -> CategoryTag(FoodCategory.UNKNOWN, "Vegetarian: unknown")
    }

    val vegan = when {
        FoodCategory.NOT_VEGAN in set -> CategoryTag(FoodCategory.NOT_VEGAN, FoodCategory.NOT_VEGAN.defaultLabel())
        FoodCategory.VEGAN in set -> CategoryTag(FoodCategory.VEGAN, FoodCategory.VEGAN.defaultLabel())
        else -> CategoryTag(FoodCategory.UNKNOWN, "Vegan: unknown")
    }

    return listOf(halal, vegetarian, vegan)
}
