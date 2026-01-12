// ui/screens/detail/ProductDetailUiState.kt
package de.htw_berlin.productscannerapp.ui.screens.detail

import de.htw_berlin.productscannerapp.ui.components.CategoryTag

data class ProductDetailUiState(
    val name: String,
    val brand: String?,
    val barcode: String,

    val imageUrl: String? = null,
    val quantity: String? = null,
    val nutriScoreGrade: String? = null,
    val offCategories: String? = null,
    val offCategoriesTags: List<String>? = null,

    val categories: List<CategoryTag>,
    val reasons: List<String>,
    val ingredients: String?,
)
