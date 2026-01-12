package de.htw_berlin.productscannerapp.data.classification

import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import de.htw_berlin.productscannerapp.ui.components.defaultLabel
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

    private fun normTag(raw: String): String =
        normalizeForMatch(raw).trim()

    private fun tagKey(raw: String): String =
        normTag(raw).substringAfter(":", normTag(raw)) // "en:vegan" -> "vegan"

    fun classify(
        ingredientsText: String?,
        ingredientsAnalysisTags: List<String>? = null,
        labelsTags: List<String>? = null
    ): Result {

        val ing = normalizeForMatch(ingredientsText).trim()
        val offAnalysis = (ingredientsAnalysisTags ?: emptyList()).map(::tagKey).toSet()
        val offLabels = (labelsTags ?: emptyList()).map(::tagKey).toSet()

        val reasons = mutableListOf<String>()
        val tags = mutableSetOf<FoodCategory>()

        // =========================
        // A) OFF tags (strong signals)
        // =========================
        val offNonVegan = "non-vegan" in offAnalysis
        val offVegan = "vegan" in offAnalysis
        val offNonVegetarian = "non-vegetarian" in offAnalysis
        val offVegetarian = "vegetarian" in offAnalysis

        if (offNonVegetarian) {
            tags += FoodCategory.NOT_VEGETARIAN
            reasons += "Open Food Facts: ingredients analysis says non-vegetarian."
        }
        if (offNonVegan) {
            tags += FoodCategory.NOT_VEGAN
            reasons += "Open Food Facts: ingredients analysis says non-vegan."
        }

        if (!tags.contains(FoodCategory.NOT_VEGAN) && offVegan) {
            tags += FoodCategory.VEGAN
            reasons += "Open Food Facts: ingredients analysis says vegan."
        }
        if (!tags.contains(FoodCategory.NOT_VEGETARIAN) && offVegetarian) {
            tags += FoodCategory.VEGETARIAN
            reasons += "Open Food Facts: ingredients analysis says vegetarian."
        }

        // Only show HALAL if OFF explicitly provides it (no inference!)
        val offHalal = offLabels.any { it == "halal" || it.contains("halal") }
        if (offHalal) {
            tags += FoodCategory.HALAL
            reasons += "Open Food Facts: halal label tag found."
        }

        // =========================
        // B) Ingredient keyword fallback (best-effort)
        // =========================
        if (ing.isBlank()) {
            if (tags.isEmpty()) {
                tags += FoodCategory.UNKNOWN
                reasons += "No ingredients text found."
            }
            return finalize(tags, reasons)
        }

        // meat (EN + DE + FR)
        val meatKeywords = listOf(
            // EN
            "beef","pork","chicken","turkey","lamb","veal","duck",
            "bacon","ham","sausage","meat","gelatin","gelatine",
            // DE
            "fleisch","rind","rindfleisch","schwein","schweinefleisch","huhn","hahnchen","pute","lamm",
            "speck","schinken","wurst","gelatine",
            // FR
            "viande","porc","boeuf","poulet","dinde","agneau","jambon","saucisse","gelatine","gélatine"
        )

        val porkSpecific = listOf(
            "pork","bacon","ham","lard",
            "schwein","schweinefleisch","speck","schinken","schmalz",
            "porc","jambon","lard"
        )

        val alcoholKeywords = listOf(
            "alcohol","ethanol","wine","beer","rum","brandy","liqueur",
            "bier","weizenbier","wein","sekt","likor","liqueur",
            "biere","vin","rhum"
        )

        val nonVeganKeywords = listOf(
            "milk","butter","cream","cheese","whey","casein","lactose",
            "egg","eggs","honey","yogurt",
            // DE
            "milch","butter","sahne","rahm","kase","käse","molke","kasein","casein","laktose",
            "ei","eier","honig","joghurt",
            // FR
            "lait","beurre","creme","crème","fromage","lactose","oeuf","œuf","miel","yaourt"
        )

        val hasMeat = meatKeywords.any { ing.contains(it) }
        val hasPork = porkSpecific.any { ing.contains(it) }
        val hasAlcohol = alcoholKeywords.any { ing.contains(it) }
        val hasNonVegan = nonVeganKeywords.any { ing.contains(it) }
        val hasGelatin = ing.contains("gelatin") || ing.contains("gelatine") || ing.contains("gélatine")

        // Non-halal signals (conservative)
        if (hasPork || hasAlcohol || hasGelatin) {
            tags += FoodCategory.NON_HALAL
            reasons += "Detected pork/alcohol/gelatin in ingredients → non-halal (best-effort)."
        }

        if (hasMeat) {
            tags += FoodCategory.NOT_VEGETARIAN
            reasons += "Detected meat keyword(s) in ingredients → not vegetarian (best-effort)."
        }

        if (hasNonVegan) {
            tags += FoodCategory.NOT_VEGAN
            reasons += "Detected dairy/egg/honey in ingredients → not vegan (best-effort)."
        }

        // Positive inferences only when not contradicted
        if (!tags.contains(FoodCategory.NOT_VEGAN) && !tags.contains(FoodCategory.NOT_VEGETARIAN)) {
            tags += FoodCategory.VEGAN
            reasons += "No meat + no animal-derived keywords detected → likely vegan (best-effort)."
        } else if (!tags.contains(FoodCategory.NOT_VEGETARIAN)) {
            tags += FoodCategory.VEGETARIAN
            reasons += "No meat keywords detected → likely vegetarian (best-effort)."
        }

        // IMPORTANT: do NOT infer HALAL from “no bad keywords found”
        // (HALAL only comes from OFF labels_tags)

        return finalize(tags, reasons)
    }

    private fun finalize(tags: Set<FoodCategory>, reasons: List<String>): Result {
        val order = listOf(
            FoodCategory.NON_HALAL,
            FoodCategory.NOT_VEGETARIAN,
            FoodCategory.NOT_VEGAN,
            FoodCategory.VEGAN,
            FoodCategory.VEGETARIAN,
            FoodCategory.HALAL,
            FoodCategory.UNKNOWN
        )

        val finalCats = order.filter { it in tags }.distinct()

        return Result(
            tags = finalCats.map { CategoryTag(category = it, label = it.defaultLabel()) },
            reasons = reasons.distinct()
        )
    }
}
