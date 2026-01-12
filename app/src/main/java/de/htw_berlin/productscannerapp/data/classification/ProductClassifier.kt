package de.htw_berlin.productscannerapp.data.classification

import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import de.htw_berlin.productscannerapp.ui.components.defaultLabel
import java.text.Normalizer
import java.util.Locale

object ProductClassifier {

    data class Result(
        val tags: List<CategoryTag>,   // ALWAYS 3 tags (Halal, Vegetarian, Vegan)
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

    // Unicode-safe "word boundary" (avoids beer matching beere, ham matching something inside a word, etc.)
    private fun word(vararg words: String): List<Regex> =
        words.map { w -> Regex("""(?<!\p{L})${Regex.escape(w)}(?!\p{L})""") }

    private fun containsAny(text: String, patterns: List<Regex>): Boolean =
        patterns.any { it.containsMatchIn(text) }

    // ---- Patterns (normalized text, so "gélatine" becomes "gelatine") ----
    private val gelatinPatterns = word("gelatin", "gelatine")

    private val porkPatterns = word(
        // EN
        "pork", "bacon", "ham", "lard",
        // DE
        "schwein", "schweinefleisch", "speck", "schinken", "schmalz",
        // FR
        "porc", "jambon", "lard"
    )

    private val alcoholPatterns = word(
        // EN
        "alcohol", "ethanol", "wine", "beer", "rum", "brandy", "liqueur",
        // DE  (include explicit compounds)
        "wein", "bier", "weizenbier", "sekt", "likor",
        // FR
        "vin", "biere", "rhum"
    )

    private val meatPatterns = word(
        // EN
        "beef","pork","chicken","turkey","lamb","veal","duck",
        "bacon","ham","sausage","meat",
        // DE
        "fleisch","rind","rindfleisch","schwein","schweinefleisch","huhn","hahnchen","pute","lamm",
        "speck","schinken","wurst",
        // FR
        "viande","porc","boeuf","poulet","dinde","agneau","jambon","saucisse"
    ) + gelatinPatterns // gelatin also implies non-vegetarian in many products (best-effort)

    private val nonVeganPatterns = word(
        // EN
        "milk","butter","cream","cheese","whey","casein","lactose",
        "egg","eggs","honey","yogurt",
        // DE (normalized: käse -> kase)
        "milch","butter","sahne","rahm","kase","molke","kasein","casein","laktose",
        "ei","eier","honig","joghurt",
        // FR (normalized: crème -> creme, œuf -> oeuf)
        "lait","beurre","creme","fromage","lactose","oeuf","miel","yaourt"
    )

    fun classify(
        ingredientsText: String?,
        ingredientsAnalysisTags: List<String>? = null,
        labelsTags: List<String>? = null
    ): Result {

        val ing = normalizeForMatch(ingredientsText).trim()
        val offAnalysis = (ingredientsAnalysisTags ?: emptyList()).map(::tagKey).toSet()
        val offLabels = (labelsTags ?: emptyList()).map(::tagKey).toSet()

        val reasons = mutableListOf<String>()

        // =========================
        // A) OFF tags (strong signals)
        // =========================
        val offNonVegan = "non-vegan" in offAnalysis
        val offVegan = "vegan" in offAnalysis
        val offNonVegetarian = "non-vegetarian" in offAnalysis
        val offVegetarian = "vegetarian" in offAnalysis

        val offHalal = offLabels.any { it == "halal" || it.contains("halal") } // positive only

        if (offNonVegetarian) reasons += "Open Food Facts: ingredients analysis says non-vegetarian."
        if (offNonVegan) reasons += "Open Food Facts: ingredients analysis says non-vegan."
        if (offVegan) reasons += "Open Food Facts: ingredients analysis says vegan."
        if (offVegetarian) reasons += "Open Food Facts: ingredients analysis says vegetarian."
        if (offHalal) reasons += "Open Food Facts: halal label tag found."

        // =========================
        // B) Ingredient keyword fallback (best-effort)
        // =========================
        val hasGelatin = if (ing.isBlank()) false else containsAny(ing, gelatinPatterns)
        val hasPork = if (ing.isBlank()) false else containsAny(ing, porkPatterns)
        val hasAlcohol = if (ing.isBlank()) false else containsAny(ing, alcoholPatterns)
        val hasMeat = if (ing.isBlank()) false else containsAny(ing, meatPatterns)
        val hasNonVegan = if (ing.isBlank()) false else containsAny(ing, nonVeganPatterns)

        // IMPORTANT: word-boundary matching prevents "beer" in "beere"
        if (!ing.isBlank()) {
            if (hasAlcohol) reasons += "Detected alcohol keyword (word-match) in ingredients → non-halal (best-effort)."
            if (hasPork) reasons += "Detected pork keyword (word-match) in ingredients → non-halal (best-effort)."
            if (hasGelatin) reasons += "Detected gelatin keyword (word-match) in ingredients → non-halal (best-effort)."
            if (hasMeat) reasons += "Detected meat keyword (word-match) in ingredients → not vegetarian (best-effort)."
            if (hasNonVegan) reasons += "Detected dairy/egg/honey keyword (word-match) in ingredients → not vegan (best-effort)."
        } else {
            reasons += "Ingredients text is empty → using OFF tags only."
        }

        // =========================
        // Decide the 3 outputs (always)
        // =========================

        // Halal dimension:
        val isNonHalal = hasPork || hasAlcohol || hasGelatin
        val halalTag = when {
            isNonHalal -> CategoryTag(FoodCategory.NON_HALAL, FoodCategory.NON_HALAL.defaultLabel())
            offHalal -> CategoryTag(FoodCategory.HALAL, FoodCategory.HALAL.defaultLabel())
            else -> CategoryTag(FoodCategory.UNKNOWN, "Halal: Unknown")
        }

        // Vegetarian dimension:
        val isNotVegetarian = offNonVegetarian || hasMeat
        val vegetarianTag = when {
            isNotVegetarian -> CategoryTag(FoodCategory.NOT_VEGETARIAN, FoodCategory.NOT_VEGETARIAN.defaultLabel())
            (!offNonVegetarian && offVegetarian) -> CategoryTag(FoodCategory.VEGETARIAN, FoodCategory.VEGETARIAN.defaultLabel())
            // safe-ish inference only if we have ingredients and no meat hit
            (!ing.isBlank() && !hasMeat) -> CategoryTag(FoodCategory.VEGETARIAN, FoodCategory.VEGETARIAN.defaultLabel())
            else -> CategoryTag(FoodCategory.UNKNOWN, "Vegetarian: Unknown")
        }

        // Vegan dimension:
        val isNotVegan = offNonVegan || hasNonVegan || hasMeat // meat implies non-vegan too
        val veganTag = when {
            isNotVegan -> CategoryTag(FoodCategory.NOT_VEGAN, FoodCategory.NOT_VEGAN.defaultLabel())
            (!offNonVegan && offVegan) -> CategoryTag(FoodCategory.VEGAN, FoodCategory.VEGAN.defaultLabel())
            // safe-ish inference only if we have ingredients and neither meat nor non-vegan hit
            (!ing.isBlank() && !hasMeat && !hasNonVegan) -> CategoryTag(FoodCategory.VEGAN, FoodCategory.VEGAN.defaultLabel())
            else -> CategoryTag(FoodCategory.UNKNOWN, "Vegan: Unknown")
        }

        return Result(
            tags = listOf(halalTag, vegetarianTag, veganTag),
            reasons = reasons.distinct()
        )
    }
}
