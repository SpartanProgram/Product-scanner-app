package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.model.Product
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    val items: Flow<List<Product>>
    suspend fun add(product: Product)
    suspend fun clear()
}
