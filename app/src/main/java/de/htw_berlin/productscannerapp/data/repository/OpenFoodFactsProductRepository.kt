package de.htw_berlin.productscannerapp.data.repository

import android.util.Log
import de.htw_berlin.productscannerapp.data.classification.ProductClassifier
import de.htw_berlin.productscannerapp.data.local.db.CachedProductEntity
import de.htw_berlin.productscannerapp.data.local.db.ProductDao
import de.htw_berlin.productscannerapp.data.model.Product
import de.htw_berlin.productscannerapp.data.remote.off.OpenFoodFactsApi
import java.io.IOException

class OpenFoodFactsProductRepository(
    private val api: OpenFoodFactsApi,
    private val dao: ProductDao
) : ProductRepository {

    override suspend fun getProduct(barcode: String): Product? {
        val normalized = barcode.trim().filter(Char::isDigit)

        // Try network first
        val fromNetwork: Product? = try {
            // pass lc (German readable data when available)
            val resp = api.getProduct(normalized, lc = "de")

            Log.d("OFF", "barcode=$normalized status=${resp.status} productNull=${resp.product == null}")

            val ok = (resp.status == 1) && (resp.product != null)
            if (!ok) null else {
                val dto = resp.product!!

                val name = dto.productName?.takeIf { it.isNotBlank() } ?: "Unknown product"
                val brand = dto.brands?.takeIf { it.isNotBlank() }
                val ingredients = dto.ingredientsText?.takeIf { it.isNotBlank() }

                val imageUrl = dto.imageFrontUrl?.takeIf { it.isNotBlank() }
                val quantity = dto.quantity?.takeIf { it.isNotBlank() }
                val nutriScoreGrade = dto.nutriscoreGrade?.takeIf { it.isNotBlank() }
                val offCategories = dto.categories?.takeIf { it.isNotBlank() }
                val offCategoriesTags = dto.categoriesTags

                // Classification (OFF tags + ingredients)
                val cls = ProductClassifier.classify(
                    ingredientsText = ingredients,
                    ingredientsAnalysisTags = dto.ingredientsAnalysisTags,
                    labelsTags = dto.labelsTags
                )

                // Cache for offline use (still only caching core fields)
                dao.upsert(
                    CachedProductEntity(
                        barcode = normalized,
                        name = name,
                        brand = brand,
                        ingredients = ingredients,
                        updatedAtMillis = System.currentTimeMillis()
                    )
                )

                Product(
                    barcode = normalized,
                    name = name,
                    brand = brand,
                    ingredients = ingredients,

                    imageUrl = imageUrl,
                    quantity = quantity,
                    nutriScoreGrade = nutriScoreGrade,
                    offCategories = offCategories,
                    offCategoriesTags = offCategoriesTags,

                    categories = cls.tags,
                    reasons = cls.reasons
                )
            }
        } catch (e: IOException) {
            Log.e("OFF", "Network IO failed for $normalized", e)
            null
        } catch (e: Exception) {
            Log.e("OFF", "Network failed for $normalized", e)
            null
        }

        if (fromNetwork != null) return fromNetwork

        // 2) Offline fallback (Room) + re-classify from cached ingredients
        val cached = dao.getByBarcode(normalized) ?: return null
        val cls = ProductClassifier.classify(
            ingredientsText = cached.ingredients,
            ingredientsAnalysisTags = null,
            labelsTags = null
        )

        return Product(
            barcode = cached.barcode,
            name = cached.name,
            brand = cached.brand,
            ingredients = cached.ingredients,

            // Offline: we didn't cache these yet, so null
            imageUrl = null,
            quantity = null,
            nutriScoreGrade = null,
            offCategories = null,
            offCategoriesTags = null,

            categories = cls.tags,
            reasons = listOf("Showing cached data (offline fallback).") + cls.reasons
        )
    }
}
