package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.model.Product
import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import kotlinx.coroutines.delay

class FakeProductRepository : ProductRepository {

    private val db: Map<String, Product> = mapOf(
        "4006381333931" to Product(
            barcode = "4006381333931",
            name = "Chocolate Bar",
            brand = "Sample Brand",
            ingredients = "Sugar, Wheat Flour, Milk Powder, Cocoa Butter, Emulsifier (Soy Lecithin)",
            categories = listOf(
                CategoryTag(FoodCategory.VEGETARIAN, "Vegetarian"),
                CategoryTag(FoodCategory.UNKNOWN, "Halal: Unknown")
            ),
            reasons = listOf(
                "Contains milk powder → not vegan",
                "No certification found → halal status unknown",
                "No meat ingredients listed → vegetarian likely"
            )
        ),
        "1234567890123" to Product(
            barcode = "1234567890123",
            name = "Pasta",
            brand = "Brand X",
            ingredients = "Durum wheat semolina, water",
            categories = listOf(
                CategoryTag(FoodCategory.VEGAN, "Vegan"),
                CategoryTag(FoodCategory.HALAL, "Halal")
            ),
            reasons = listOf(
                "Only plant-based ingredients listed → vegan",
                "No animal-derived ingredients → halal likely"
            )
        )
    )

    override suspend fun getProduct(barcode: String): Product? {
        delay(900) // 👈 makes skeleton loading visible
        return db[barcode]
    }
}
