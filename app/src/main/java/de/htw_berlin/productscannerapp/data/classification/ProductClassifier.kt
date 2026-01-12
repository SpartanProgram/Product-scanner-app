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
                tags = listOf(CategoryTag(FoodCategory.UNKNOWN)),
                reasons = listOf("No ingredients text available → cannot classify reliably.")
            )
        }

        val reasons = mutableListOf<String>()
        val tags = mutableSetOf<FoodCategory>()

        // --- Very simple keyword rules (starter version) ---
        val meatKeywords = listOf(
            "beef", "pork", "chicken", "turkey", "lamb", "veal", "duck",
            "bacon", "ham", "sausage", "gelatin", "gelatine"
        )

        val nonVeganKeywords = listOf(
            "milk", "butter", "cream", "cheese", "whey", "casein",
            "egg", "eggs", "honey", "yogurt", "lactose"
        )

        val alcoholKeywords = listOf("alcohol", "ethanol", "wine", "beer", "rum", "brandy")

        val containsMeat = meatKeywords.any { ing.contains(it) }
        val containsNonVegan = nonVeganKeywords.any { ing.contains(it) }
        val containsAlcohol = alcoholKeywords.any { ing.contains(it) }

        if (containsMeat) {
            tags += FoodCategory.NOT_VEGETARIAN
            reasons += "Detected meat-related ingredient keyword(s) → contains meat."
        } else {
            // not meat → at least vegetarian (best-effort)
            tags += FoodCategory.VEGETARIAN
            reasons += "No meat keywords detected → likely vegetarian."
        }

        if (containsNonVegan) {
            tags += FoodCategory.NOT_VEGAN
            reasons += "Detected dairy/egg/honey keyword(s) → not vegan."
        } else {
            tags += FoodCategory.VEGAN
            reasons += "No dairy/egg/honey keywords detected → likely vegan."
        }

        // Halal is hard from ingredients alone, but we can at least flag “not allowed” if alcohol/pork/gelatin present
        val notAllowedKeywords = listOf("pork", "gelatin", "gelatine")
        val containsNotAllowed = notAllowedKeywords.any { ing.contains(it) } || containsAlcohol

        if (containsNotAllowed) {
            tags += FoodCategory.NON_HALAL
            reasons += "Detected alcohol/pork/gelatin keyword(s) → not allowed (best-effort)."
        } else {
            tags += FoodCategory.UNKNOWN
            reasons += "Halal cannot be confirmed from ingredients alone (needs certification/tags)."
        }

        // Convert to CategoryTag list
        val finalTags = tags
            .toList()
            .map { CategoryTag(it) }

        return Result(tags = finalTags, reasons = reasons)
    }
}
