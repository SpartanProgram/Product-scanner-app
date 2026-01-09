package de.htw_berlin.productscannerapp.ui.screens.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScanScreen(
    innerPadding: PaddingValues,
    onFakeScan: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Scan a product barcode")
                Spacer(Modifier.height(8.dp))
                Text("Camera preview will go here later.")
            }
        }

        Button(onClick = { onFakeScan("4006381333931") }) {
            Text("Simulate scan")
        }
    }
}
