package de.htw_berlin.productscannerapp.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.htw_berlin.productscannerapp.data.AppGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import de.htw_berlin.productscannerapp.data.AppGraph


sealed interface ProductDetailState {
    data object Loading : ProductDetailState
    data class Success(val data: ProductDetailUiState) : ProductDetailState
    data class Error(val message: String) : ProductDetailState
}

class ProductDetailViewModel : ViewModel() {

    private val _state = MutableStateFlow<ProductDetailState>(ProductDetailState.Loading)
    val state: StateFlow<ProductDetailState> = _state

    fun load(barcode: String) {
        _state.value = ProductDetailState.Loading

        viewModelScope.launch {
            val product = AppGraph.productRepository.getProduct(barcode)

            if (product == null) {
                _state.value = ProductDetailState.Error("Product not found for barcode: $barcode")
                return@launch
            }

            AppGraph.historyRepository.add(product)

            _state.value = ProductDetailState.Success(
                ProductDetailUiState(
                    name = product.name,
                    brand = product.brand,
                    barcode = product.barcode,
                    categories = product.categories,
                    reasons = product.reasons,
                    ingredients = product.ingredients
                )
            )

            if (product == null) {
                _state.value = ProductDetailState.Error("Product not found for barcode: $barcode")
                return@launch
            }

            _state.update {
                ProductDetailState.Success(
                    ProductDetailUiState(
                        name = product.name,
                        brand = product.brand,
                        barcode = product.barcode,
                        categories = product.categories,
                        reasons = product.reasons,
                        ingredients = product.ingredients
                    )
                )
            }
        }
    }
}
