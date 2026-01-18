package de.htw_berlin.productscannerapp.ui.screens.history

import de.htw_berlin.productscannerapp.ui.components.CategoryTag

data class HistoryItemUi(
    val barcode: String,
    val name: String,
    val brand: String?,
    val timestampLabel: String,
    val categories: List<CategoryTag>,

    val imageUrl: String? = null,
    val quantity: String? = null,
    val nutriScoreGrade: String? = null
)
