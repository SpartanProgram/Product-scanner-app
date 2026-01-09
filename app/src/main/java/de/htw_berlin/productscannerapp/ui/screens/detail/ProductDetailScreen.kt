package de.htw_berlin.productscannerapp.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    barcode: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Product detail", style = MaterialTheme.typography.titleMedium)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Barcode: $barcode")
                Spacer(Modifier.height(8.dp))
                Text("Categories will appear here (Halal / Vegan / etc.)")
                Spacer(Modifier.height(8.dp))
                Text("Reasons/explanations will appear here.")
            }
        }
    }
}
