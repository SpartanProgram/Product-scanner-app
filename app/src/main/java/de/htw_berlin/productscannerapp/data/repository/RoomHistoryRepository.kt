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
                val categories = e.categoriesCsv
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .mapNotNull { raw -> runCatching { FoodCategory.valueOf(raw) }.getOrNull() }
                    .map { cat -> CategoryTag(category = cat) }

                Product(
                    barcode = e.barcode,
                    name = e.name,
                    brand = e.brand,
                    ingredients = e.ingredients,
                    categories = categories,
                    reasons = emptyList()
                )
            }
        }

    override suspend fun add(product: Product) {
        val csv = product.categories.joinToString(",") { it.category.name }

        dao.upsert(
            HistoryEntity(
                barcode = product.barcode,
                name = product.name,
                brand = product.brand,
                ingredients = product.ingredients,
                categoriesCsv = csv,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clear() {
        dao.clearAll()
    }
}
