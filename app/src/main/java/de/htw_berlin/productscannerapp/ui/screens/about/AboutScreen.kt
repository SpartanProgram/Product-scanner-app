package de.htw_berlin.productscannerapp.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("About", style = MaterialTheme.typography.titleMedium)

        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Product Scanner App", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Scan barcodes and classify products as Halal / Non-Halal / Vegetarian / Vegan / Unknown.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tech stack", style = MaterialTheme.typography.titleSmall)
                Text("• Kotlin + Jetpack Compose")
                Text("• Navigation + ViewModels + Coroutines")
                Text("• Barcode scanning: Google Code Scanner (Play services)")
                Text("• Product data: Open Food Facts")
            }
        }

        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Notes", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Results are best-effort based on ingredient labels and available metadata. "
                            + "Always verify with official certification when required.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
