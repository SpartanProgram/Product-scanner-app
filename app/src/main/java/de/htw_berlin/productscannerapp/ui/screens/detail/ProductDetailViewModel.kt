package de.htw_berlin.productscannerapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import de.htw_berlin.productscannerapp.ui.components.CategoryTag
import de.htw_berlin.productscannerapp.ui.components.FoodCategory

sealed interface ProductDetailState {
    data object Loading : ProductDetailState
    data class Success(val data: ProductDetailUiState) : ProductDetailState
    data class Error(val message: String) : ProductDetailState
}

class ProductDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow<ProductDetailState>(ProductDetailState.Loading)
    val state: StateFlow<ProductDetailState> = _state

    fun load(barcode: String) {
        // FRONTEND placeholder: your backend teammate will replace this with real repository call
        _state.update {
            ProductDetailState.Success(
                ProductDetailUiState(
                    name = "Sample Product",
                    brand = "Brand Name",
                    barcode = barcode,
                    categories = listOf(
                        CategoryTag(FoodCategory.VEGETARIAN, "Vegetarian"),
                        CategoryTag(FoodCategory.UNKNOWN, "Unknown")
                    ),
                    reasons = listOf(
                        "No meat ingredients listed → vegetarian likely",
                        "No certification found → halal unknown"
                    ),
                    ingredients = "Sugar, Wheat Flour, Milk Powder, Cocoa Butter"
                )
            )
        }
    }
}
