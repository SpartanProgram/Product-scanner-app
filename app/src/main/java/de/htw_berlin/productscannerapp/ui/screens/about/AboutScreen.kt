package de.htw_berlin.productscannerapp.ui.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text("About")
        Spacer(Modifier.height(8.dp))
        Text("This app classifies products into Halal / Non-Halal / Vegetarian / Vegan / Unknown.")
        Spacer(Modifier.height(8.dp))
        Text("Disclaimer: Best-effort classification based on available data.")
    }
}
