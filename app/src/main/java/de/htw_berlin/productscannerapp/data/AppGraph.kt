package de.htw_berlin.productscannerapp.data

import de.htw_berlin.productscannerapp.data.repository.*

object AppGraph {
    val productRepository: ProductRepository = FakeProductRepository()
    val historyRepository: HistoryRepository = InMemoryHistoryRepository()
}
