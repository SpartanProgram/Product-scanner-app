package de.htw_berlin.productscannerapp.data.classification

import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import java.util.Locale

object ProductClassifier {

    data class Result(
        val tags: List<CategoryTag>,
        val reasons: List<String>
    )

    fun classify(ingredientsText: String?): Result {
        val ing = ingredientsText?.lowercase(Locale.ROOT).orEmpty()

        if (ing.isBlank()) {
            return Result(
                tags = listOf(CategoryTag(category = FoodCategory.UNKNOWN)),
                reasons = listOf("No ingredients text.")
            )
        }

        // TODO: your rules here
        return Result(
            tags = listOf(CategoryTag(category = FoodCategory.UNKNOWN)),
            reasons = listOf("Classification not implemented yet.")
        )
    }
}
