package de.htw_berlin.productscannerapp.data.repository

import de.htw_berlin.productscannerapp.data.model.Product

interface ProductRepository {
    suspend fun getProduct(barcode: String): Product?
}
