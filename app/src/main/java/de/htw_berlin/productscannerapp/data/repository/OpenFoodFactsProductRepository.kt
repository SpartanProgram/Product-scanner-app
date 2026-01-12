package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.local.db.CachedProductEntity
import de.htw_berlin.productscannerapp.data.local.db.ProductDao
import de.htw_berlin.productscannerapp.data.model.Product
import de.htw_berlin.productscannerapp.data.remote.off.OpenFoodFactsApi
import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import java.io.IOException
import android.util.Log


class OpenFoodFactsProductRepository(
    private val api: OpenFoodFactsApi,
    private val dao: ProductDao
) : ProductRepository {

    override suspend fun getProduct(barcode: String): Product? {
        val normalized = barcode.trim().filter(Char::isDigit)

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

                val product = Product(
                    barcode = normalized,
                    name = name,
                    brand = brand,
                    ingredients = ingredients,
                    categories = listOf(CategoryTag(FoodCategory.UNKNOWN, "Unknown")),
                    reasons = listOf(
                        "Data fetched from Open Food Facts.",
                        "Classification logic will be applied on ingredients and tags."
                    )
                )

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
        return Product(
            barcode = cached.barcode,
            name = cached.name,
            brand = cached.brand,
            ingredients = cached.ingredients,
            categories = listOf(CategoryTag(FoodCategory.UNKNOWN, "Unknown")),
            reasons = listOf("Showing cached data (offline fallback).")
        )
    }
}
