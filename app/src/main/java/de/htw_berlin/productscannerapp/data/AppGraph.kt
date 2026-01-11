package de.htw_berlin.productscannerapp.data

import de.htw_berlin.productscannerapp.data.repository.FakeProductRepository
import de.htw_berlin.productscannerapp.data.repository.ProductRepository

object AppGraph {
    val productRepository: ProductRepository = FakeProductRepository()
}
