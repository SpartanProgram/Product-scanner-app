package de.htw_berlin.productscannerapp.ui.screens.detail

import de.htw_berlin.productscannerapp.ui.components.CategoryTag

data class ProductDetailUiState(
    val name: String,
    val brand: String?,
    val barcode: String,
    val categories: List<CategoryTag>,
    val reasons: List<String>,
    val ingredients: String?,
)
