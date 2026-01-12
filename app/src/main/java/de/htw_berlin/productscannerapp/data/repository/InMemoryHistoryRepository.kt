package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InMemoryHistoryRepository : HistoryRepository {

    private val _items = MutableStateFlow<List<Product>>(emptyList())
    override val items: StateFlow<List<Product>> = _items

    override suspend fun add(product: Product) {
        _items.update { current ->
            val without = current.filterNot { it.barcode == product.barcode }
            listOf(product) + without
        }
    }

    override suspend fun clear() {
        _items.value = emptyList()
    }
}
