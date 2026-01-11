package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.lifecycle.ViewModel
import de.htw_berlin.productscannerapp.data.AppGraph
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted

class HistoryViewModel : ViewModel() {

    val items: StateFlow<List<HistoryItemUi>> =
        AppGraph.historyRepository.items
            .map { products ->
                products.map { p ->
                    HistoryItemUi(
                        barcode = p.barcode,
                        name = p.name,
                        brand = p.brand,
                        timestampLabel = "Recently",
                        categories = p.categories
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun clear() {
        AppGraph.historyRepository.clear()
    }
}
