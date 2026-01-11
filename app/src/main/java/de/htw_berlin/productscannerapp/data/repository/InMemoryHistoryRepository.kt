package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class InMemoryHistoryRepository : HistoryRepository {
    private val _items = MutableStateFlow<List<Product>>(emptyList())
    override val items: StateFlow<List<Product>> = _items

    override fun add(product: Product) {
        _items.update { current ->
            // keep newest first, no duplicates by barcode
            val without = current.filterNot { it.barcode == product.barcode }
            listOf(product) + without
        }
    }

    override fun clear() {
        _items.value = emptyList()
    }
}
