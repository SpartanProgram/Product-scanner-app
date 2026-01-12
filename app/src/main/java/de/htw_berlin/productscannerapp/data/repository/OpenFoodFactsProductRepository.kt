package de.htw_berlin.productscannerapp.data.repository

import android.util.Log
import de.htw_berlin.productscannerapp.data.local.db.CachedProductEntity
import de.htw_berlin.productscannerapp.data.local.db.ProductDao
import de.htw_berlin.productscannerapp.data.model.Product
import de.htw_berlin.productscannerapp.data.remote.off.OpenFoodFactsApi
import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import java.io.IOException
import de.htw_berlin.productscannerapp.data.classification.ProductClassifier

class OpenFoodFactsProductRepository(
    private val api: OpenFoodFactsApi,
    private val dao: ProductDao
) : ProductRepository {

    override suspend fun getProduct(barcode: String): Product? {
        val normalized = barcode.trim().filter(Char::isDigit)
        if (normalized.isBlank()) return null

        // 1) Try network first
        val fromNetwork: Product? = try {
            val resp = api.getProduct(normalized)
            Log.d("OFF", "barcode=$normalized status=${resp.status} productNull=${resp.product == null}")

            val ok = (resp.status == 1) && (resp.product != null)
            if (!ok) null else {
                val dto = resp.product!!

                val name = dto.productName?.takeIf { it.isNotBlank() } ?: "Unknown product"
                val brand = dto.brands?.takeIf { it.isNotBlank() }
                val ingredients = dto.ingredientsText?.takeIf { it.isNotBlank() }

                val categories = mutableListOf<CategoryTag>()
                val reasons = mutableListOf<String>()

                reasons += "Data fetched from Open Food Facts."

                val analysis = dto.ingredientsAnalysisTags
                val labels = dto.labelsTags
                val allergens = dto.allergensTags
                val offCategories = dto.categoriesTags

                // --- Vegan / Vegetarian from OFF ingredient analysis tags ---
                val vegan = when {
                    hasTag(analysis, "en:vegan") -> true
                    hasTag(analysis, "en:non-vegan") -> false
                    else -> null
                }
                val vegetarian = when {
                    hasTag(analysis, "en:vegetarian") -> true
                    hasTag(analysis, "en:non-vegetarian") -> false
                    else -> null
                }

                when (vegan) {
                    true -> {
                        categories += CategoryTag(FoodCategory.VEGAN, "Vegan")
                        reasons += "OFF ingredients analysis tag: vegan."
                    }
                    false -> {
                        // keep this only if your enum has NOT_VEGAN; otherwise remove
                        categories += CategoryTag(FoodCategory.NOT_VEGAN, "Not vegan")
                        reasons += "OFF ingredients analysis tag: non-vegan."
                    }
                    null -> reasons += "No OFF vegan analysis tag found."
                }

                when (vegetarian) {
                    true -> {
                        categories += CategoryTag(FoodCategory.VEGETARIAN, "Vegetarian")
                        reasons += "OFF ingredients analysis tag: vegetarian."
                    }
                    false -> {
                        // keep this only if your enum has NOT_VEGETARIAN; otherwise remove
                        categories += CategoryTag(FoodCategory.NOT_VEGETARIAN, "Not vegetarian")
                        reasons += "OFF ingredients analysis tag: non-vegetarian."
                    }
                    null -> reasons += "No OFF vegetarian analysis tag found."
                }

                // --- Halal: ONLY if explicitly tagged in labels (don’t infer!) ---
                val halalTagged =
                    hasTag(labels, "en:halal") ||
                            hasTag(labels, "en:halal-certified") ||
                            hasTag(labels, "en:halal-food")

                if (halalTagged) {
                    categories += CategoryTag(FoodCategory.HALAL, "Halal")
                    reasons += "OFF label tag indicates halal."
                } else {
                    reasons += "No halal label tag found (halal stays unknown)."
                }

                // --- Optional extra reasons (helps debugging & feels “smart”) ---
                if (!allergens.isNullOrEmpty()) {
                    reasons += "Allergens (OFF): " + allergens.take(5).joinToString { prettyTag(it) }
                }
                if (!offCategories.isNullOrEmpty()) {
                    reasons += "Categories (OFF): " + offCategories.take(3).joinToString { prettyTag(it) }
                }

                if (categories.isEmpty()) {
                    categories += CategoryTag(FoodCategory.UNKNOWN, "Unknown")
                }

                val classified = ProductClassifier.classify(ingredients)

                val product = Product(
                    barcode = normalized,
                    name = name,
                    brand = brand,
                    ingredients = ingredients,
                    categories = classified.tags,
                    reasons = listOf("Data fetched from Open Food Facts.") + classified.reasons
                )

                // Cache for offline use
                dao.upsert(
                    CachedProductEntity(
                        barcode = normalized,
                        name = name,
                        brand = brand,
                        ingredients = ingredients,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                )

                product
            }
        } catch (e: IOException) {
            Log.e("OFF", "Network IO failed for $normalized", e)
            null
        } catch (e: Exception) {
            Log.e("OFF", "Network failed for $normalized", e)
            null
        }

        if (fromNetwork != null) return fromNetwork

        // 2) Offline fallback (Room)
        val cached = dao.getByBarcode(normalized) ?: return null
        val classified = ProductClassifier.classify(cached.ingredients)

        return Product(
            barcode = cached.barcode,
            name = cached.name,
            brand = cached.brand,
            ingredients = cached.ingredients,
            categories = classified.tags,
            reasons = listOf("Showing cached data (offline fallback).") + classified.reasons
        )
    }
}

/** Helper: exact tag match (OFF tags are often like "en:vegan") */
private fun hasTag(tags: List<String>?, tag: String): Boolean =
    tags?.any { it.equals(tag, ignoreCase = true) } == true

/** Helper: nicer display for tags like "en:palm-oil" -> "palm oil" */
private fun prettyTag(tag: String): String =
    tag.removePrefix("en:").replace('-', ' ')
