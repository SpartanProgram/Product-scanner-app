package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    innerPadding: PaddingValues,
    onOpenItem: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recent scans", style = MaterialTheme.typography.titleMedium)

        Card(modifier = Modifier.fillMaxWidth().clickable { onOpenItem("1234567890123") }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Sample Product")
                Text("Barcode: 1234567890123", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
