package de.htw_berlin.productscannerapp.data.classification

import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import java.text.Normalizer
import java.util.Locale

object ProductClassifier {

    data class Result(
        val tags: List<CategoryTag>,
        val reasons: List<String>
    )

    private fun normalizeForMatch(raw: String?): String {
        val s = raw.orEmpty().lowercase(Locale.ROOT)
        val noDiacritics = Normalizer.normalize(s, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
        return noDiacritics.replace("ß", "ss")
    }

    private fun String.containsAnyRegex(vararg patterns: Regex): Boolean =
        patterns.any { it.containsMatchIn(this) }

    fun classify(ingredientsText: String?): Result {
        val ing = normalizeForMatch(ingredientsText).trim()

        if (ing.isBlank()) {
            return Result(
                tags = listOf(CategoryTag(FoodCategory.UNKNOWN)),
                reasons = listOf("No ingredients text found.")
            )
        }

        // =========================
        // 2) Upgraded keyword rules (DE/FR/EN)
        // =========================

        // 🥩 Meat / Not vegetarian
        val meatPatterns = arrayOf(
            Regex("""\b(meat|viande|fleisch)\b"""),
            Regex("""\b(beef|boeuf|rind|rindfleisch)\b"""),
            Regex("""\b(pork|porc|schwein|schweinefleisch)\b"""),
            Regex("""\b(bacon|speck|schinken|jambon|lard|saindoux)\b"""),
            Regex("""\b(salami|wurst|bratwurst|saucisson|saucisse)\b"""),
            Regex("""\b(chicken|poulet|huhn|hahnchen|haehnchen)\b"""),
            Regex("""\b(turkey|dinde|pute|truthahn)\b"""),
            Regex("""\b(duck|canard|ente)\b"""),
            Regex("""\b(goose|oie|gans)\b"""),
            Regex("""\b(lamb|agneau|lamm)\b"""),
            Regex("""\b(veal|veau|kalb|kalbfleisch)\b"""),
            Regex("""\b(game|gibier|wild)\b""")
        )

        // ❌ Pork (for "not allowed")
        val porkPatterns = arrayOf(
            Regex("""\b(pork|porc|schwein|schweinefleisch)\b"""),
            Regex("""\b(bacon|speck|schinken|jambon|lard|saindoux)\b""")
        )

        // ❌ Gelatin (best-effort)
        val gelatinPatterns = arrayOf(
            Regex("""\b(gelatin|gelatine|gelatina)\b"""),
            Regex("""\b(e441)\b""")
        )

        // ❌ Alcohol
        // NOTE: avoid false positive for "alkoholfrei" / "sans alcool"
        val isAlcoholFree = ing.contains("alkoholfrei") || ing.contains("sans alcool")

        val alcoholPatterns = arrayOf(
            Regex("""\b(alcohol|alcool|alkohol|ethanol)\b"""),
            Regex("""\b(wine|vin|wein|weinbrand)\b"""),
            Regex("""\b(beer|biere|bier|weizenbier)\b"""),
            Regex("""\b(rum|rhum|brandy|cognac|whisky|whiskey|vodka|liqueur|likor|likoer)\b""")
        )

        // 🚫 Not vegan (dairy/egg/honey)
        val nonVeganPatterns = arrayOf(
            Regex("""\b(milk|lait|milch)\b"""),
            Regex("""\b(butter|beurre)\b"""),
            Regex("""\b(cream|creme|sahne|rahm)\b"""),
            Regex("""\b(cheese|fromage|kaese|kase)\b"""),
            Regex("""\b(whey|molke|lactoserum|petit[- ]lait)\b"""),
            Regex("""\b(casein|kasein)\b"""),
            Regex("""\b(lactose|laktose)\b"""),
            Regex("""\b(egg|oeuf|eier)\b"""),
            Regex("""\b(honey|miel|honig)\b"""),
            Regex("""\b(yogurt|yaourt|joghurt|yoghurt)\b""")
        )

        val reasons = mutableListOf<String>()
        val tags = mutableListOf<FoodCategory>()

        val hasMeat = ing.containsAnyRegex(*meatPatterns)
        val hasPork = ing.containsAnyRegex(*porkPatterns)
        val hasGelatin = ing.containsAnyRegex(*gelatinPatterns)
        val hasAlcohol = !isAlcoholFree && ing.containsAnyRegex(*alcoholPatterns)
        val hasNonVegan = ing.containsAnyRegex(*nonVeganPatterns)

        // 1) ❌ Not allowed (NON_HALAL)
        if (hasPork || hasAlcohol || hasGelatin) {
            tags += FoodCategory.NON_HALAL
            reasons += "Detected pork/alcohol/gelatin keyword(s) → not allowed (best-effort)."
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

        // 5) Allowed (HALAL) only if not flagged as NON_HALAL
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
