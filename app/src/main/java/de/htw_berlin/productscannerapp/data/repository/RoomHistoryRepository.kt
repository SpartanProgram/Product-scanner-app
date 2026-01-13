// data/repository/RoomHistoryRepository.kt
package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.local.db.HistoryDao
import de.htw_berlin.productscannerapp.data.local.db.HistoryEntity
import de.htw_berlin.productscannerapp.data.model.Product
import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory
import de.htw_berlin.productscannerapp.ui.components.defaultLabel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomHistoryRepository(
    private val dao: HistoryDao
) : HistoryRepository {

    override val items: Flow<List<Product>> =
        dao.observeAll().map { list ->
            list.map { e ->
                val cats = e.categoriesCsv
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .take(3)
                    .mapIndexed { index, raw ->
                        val cat = runCatching { FoodCategory.valueOf(raw) }.getOrNull() ?: FoodCategory.UNKNOWN
                        CategoryTag(category = cat, label = labelForDimension(cat, index))
                    }
                    .padTo3()

                val reasons = e.reasonsText
                    .split("\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .ifEmpty { listOf("Saved from history.") }

                Product(
                    barcode = e.barcode,
                    name = e.name,
                    brand = e.brand,
                    ingredients = e.ingredients,

                    imageUrl = e.imageUrl,
                    quantity = e.quantity,
                    nutriScoreGrade = e.nutriScoreGrade,
                    offCategories = null,
                    offCategoriesTags = null,

                    categories = cats,
                    reasons = reasons
                )
            }
        }

    override suspend fun add(product: Product) {
        val three = product.categories.take(3).padTo3()
        val csv = three.joinToString(",") { it.category.name }

        val reasonsText = product.reasons
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString("\n")

        dao.upsert(
            HistoryEntity(
                barcode = product.barcode,
                name = product.name,
                brand = product.brand,
                ingredients = product.ingredients,
                categoriesCsv = csv,
                reasonsText = reasonsText,

                imageUrl = product.imageUrl,
                quantity = product.quantity,
                nutriScoreGrade = product.nutriScoreGrade,
                offCategories = product.offCategories,

                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clear() = dao.clearAll()

    private fun labelForDimension(cat: FoodCategory, index: Int): String {
        return if (cat != FoodCategory.UNKNOWN) {
            cat.defaultLabel()
        } else {
            when (index) {
                0 -> "Halal: Unknown"
                1 -> "Vegetarian: Unknown"
                2 -> "Vegan: Unknown"
                else -> "Info"
            }
        }
    }

    private fun List<CategoryTag>.padTo3(): List<CategoryTag> {
        if (size >= 3) return take(3)
        val missing = 3 - size
        val fillers = (0 until missing).map { i ->
            val idx = size + i
            CategoryTag(FoodCategory.UNKNOWN, labelForDimension(FoodCategory.UNKNOWN, idx))
        }
        return this + fillers
    }
}
