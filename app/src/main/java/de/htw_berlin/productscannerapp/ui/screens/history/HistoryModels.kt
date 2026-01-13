package de.htw_berlin.productscannerapp.ui.screens.history

import de.htw_berlin.productscannerapp.ui.components.CategoryTag

data class HistoryItemUi(
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val timestampLabel: String = "Just now",
    val categories: List<CategoryTag> = emptyList(),
    val quantity: String?,
    val nutriScoreGrade: String?

)
