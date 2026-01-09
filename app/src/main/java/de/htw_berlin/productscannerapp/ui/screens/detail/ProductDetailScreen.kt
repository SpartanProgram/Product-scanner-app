package de.htw_berlin.productscannerapp.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.htw_berlin.productscannerapp.ui.components.CategoryChip

@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    barcode: String,
    vm: ProductDetailViewModel = viewModel()
) {
    // load once per barcode
    LaunchedEffect(barcode) { vm.load(barcode) }

    val state by vm.state.collectAsState()

    when (state) {
        ProductDetailState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProductDetailState.Error -> {
            val msg = (state as ProductDetailState.Error).message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Error: $msg")
            }
        }

        is ProductDetailState.Success -> {
            ProductDetailContent(
                innerPadding = innerPadding,
                state = (state as ProductDetailState.Success).data
            )
        }
    }
}

@Composable
private fun ProductDetailContent(
    innerPadding: PaddingValues,
    state: ProductDetailUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(state.name, style = MaterialTheme.typography.headlineSmall)
            state.brand?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Text(
                    "Barcode: ${state.barcode}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Text(
                    "Categories",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.categories) { tag ->
                        CategoryChip(tag = tag)
                    }
                }
            }
        }

        state.ingredients?.let { ing ->
            item {
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        "Ingredients",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(ing, modifier = Modifier.padding(16.dp))
                }
            }
        }

        item {
            Card(Modifier.fillMaxWidth()) {
                Text(
                    "Why this result?",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                if (state.reasons.isEmpty()) {
                    Text(
                        "No explanation available.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.reasons.forEach { reason ->
                        Text(
                            "• $reason",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}
