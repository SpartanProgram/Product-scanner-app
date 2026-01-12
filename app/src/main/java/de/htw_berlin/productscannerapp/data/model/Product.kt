package de.htw_berlin.productscannerapp.data.model

import de.htw_berlin.productscannerapp.ui.components.CategoryTag

data class Product(
    val barcode: String,
    val name: String,
    val brand: String?,
    val ingredients: String?,

    val imageUrl: String? = null,
    val quantity: String? = null,
    val nutriScoreGrade: String? = null,
    val offCategories: String? = null,
    val offCategoriesTags: List<String>? = null,

    val categories: List<CategoryTag>,
    val reasons: List<String>
)
