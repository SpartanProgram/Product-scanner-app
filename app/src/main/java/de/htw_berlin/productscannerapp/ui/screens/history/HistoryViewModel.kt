package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.htw_berlin.productscannerapp.data.AppGraph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
                        categories = p.categories,
                        quantity = p.quantity,
                        nutriScoreGrade = p.nutriScoreGrade
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun clear() {
        viewModelScope.launch {
            AppGraph.historyRepository.clear()
        }
    }
}
