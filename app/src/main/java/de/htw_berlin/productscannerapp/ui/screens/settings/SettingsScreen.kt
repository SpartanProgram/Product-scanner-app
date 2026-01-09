package de.htw_berlin.productscannerapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text("Settings (placeholder)")
        Spacer(Modifier.height(8.dp))
        Text("Language, start screen, etc.")
    }
}
