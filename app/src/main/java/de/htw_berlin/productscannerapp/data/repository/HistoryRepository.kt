package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.model.Product
import kotlinx.coroutines.flow.StateFlow

interface HistoryRepository {
    val items: StateFlow<List<Product>>
    fun add(product: Product)
    fun clear()
}
