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
        val ing = ingredientsText?.lowercase(Locale.ROOT)?.trim().orEmpty()

        if (ing.isBlank()) {
            return Result(
                tags = listOf(CategoryTag(FoodCategory.UNKNOWN)),
                reasons = listOf("No ingredients text found.")
            )
        }

        // 🔥 Keywords (starter set)
        val meatKeywords = listOf(
            "beef", "pork", "chicken", "turkey", "lamb", "veal", "duck",
            "bacon", "ham", "sausage", "meat", "gelatin", "gelatine"
        )

        val porkSpecific = listOf("pork", "bacon", "ham", "lard")

        val alcoholKeywords = listOf(
            "alcohol", "ethanol", "wine", "beer", "rum", "brandy", "liqueur"
        )

        val nonVeganKeywords = listOf(
            "milk", "butter", "cream", "cheese", "whey", "casein", "lactose",
            "egg", "eggs", "honey", "yogurt"
        )

        val reasons = mutableListOf<String>()
        val tags = mutableListOf<FoodCategory>()

        val hasMeat = meatKeywords.any { ing.contains(it) }
        val hasPork = porkSpecific.any { ing.contains(it) }
        val hasAlcohol = alcoholKeywords.any { ing.contains(it) }
        val hasNonVegan = nonVeganKeywords.any { ing.contains(it) }

        // 1) ❌ Not allowed (NON_HALAL)
        if (hasPork || hasAlcohol || ing.contains("gelatin") || ing.contains("gelatine")) {
            tags += FoodCategory.NON_HALAL
            reasons += "Detected alcohol/pork/gelatin keyword(s) → not allowed (best-effort)."
        }

        // 2) 🥩 Contains meat (NOT_VEGETARIAN)
        if (hasMeat) {
            tags += FoodCategory.NOT_VEGETARIAN
            reasons += "Detected meat keyword(s) → contains meat (best-effort)."
        } else {
            reasons += "No meat keywords detected → likely vegetarian."
        }

        // 3) 🚫 Not vegan (NOT_VEGAN)
        if (hasNonVegan) {
            tags += FoodCategory.NOT_VEGAN
            reasons += "Detected dairy/egg/honey keyword(s) → not vegan (best-effort)."
        }

        // 4) Positive label (VEGAN / VEGETARIAN) when safe
        val isVegetarian = !hasMeat
        val isVegan = !hasMeat && !hasNonVegan

        if (isVegan) tags += FoodCategory.VEGAN
        else if (isVegetarian) tags += FoodCategory.VEGETARIAN

        // 5) ✅ Allowed (HALAL) only if not flagged as NON_HALAL
        if (!tags.contains(FoodCategory.NON_HALAL)) {
            tags += FoodCategory.HALAL
        }

        // Keep order clean + unique
        val ordered = listOf(
            FoodCategory.NON_HALAL,
            FoodCategory.NOT_VEGETARIAN,
            FoodCategory.NOT_VEGAN,
            FoodCategory.VEGAN,
            FoodCategory.VEGETARIAN,
            FoodCategory.HALAL,
            FoodCategory.UNKNOWN
        )

        val finalCats = ordered.filter { it in tags }.distinct()

        return Result(
            tags = finalCats.map { CategoryTag(it) },
            reasons = reasons.distinct()
        )
    }
}
