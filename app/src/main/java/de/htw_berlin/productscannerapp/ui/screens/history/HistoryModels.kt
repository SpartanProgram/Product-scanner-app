package de.htw_berlin.productscannerapp.ui.screens.history

data class HistoryItemUi(
    val barcode: String,
    val name: String,
    val brand: String? = null,
    val timestampLabel: String = "Just now"
)
