package de.htw_berlin.productscannerapp.ui.screens.scan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    innerPadding: PaddingValues,
    onStartScan: () -> Unit,
    onBarcodeEntered: (String) -> Unit
) {
    var scanningEnabled by remember { mutableStateOf(true) }
    var showManual by remember { mutableStateOf(false) }
    var manualValue by remember { mutableStateOf("") }

    if (showManual) {
        ModalBottomSheet(onDismissRequest = { showManual = false }) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Enter barcode", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = manualValue,
                    onValueChange = { manualValue = it.filter(Char::isDigit) },
                    label = { Text("EAN / UPC") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    TextButton(onClick = { showManual = false }) { Text("Cancel") }
                    Button(
                        enabled = manualValue.trim().length >= 8,
                        onClick = {
                            val code = manualValue.trim()
                            showManual = false
                            onBarcodeEntered(code)
                        }
                    ) { Text("Search") }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title row + "camera" toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Scan a barcode", style = MaterialTheme.typography.titleMedium)

            IconButton(onClick = { scanningEnabled = !scanningEnabled }) {
                Icon(
                    imageVector = if (scanningEnabled) Icons.Outlined.Videocam else Icons.Outlined.VideocamOff,
                    contentDescription = if (scanningEnabled) "Scanning enabled" else "Scanning disabled"
                )
            }
        }

        // This is NOT a real preview for Code Scanner; it's a launcher surface.
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onClick = { if (scanningEnabled) onStartScan() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (scanningEnabled) "Tap to open scanner" else "Scanning is off",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (scanningEnabled)
                        "The scanner will open in a separate camera screen."
                    else
                        "Turn scanning on to open the camera scanner.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Tip: Align the barcode in the center.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        FilledTonalButton(
            onClick = { showManual = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enter barcode manually")
        }

        Button(
            onClick = { onStartScan() },
            modifier = Modifier.fillMaxWidth(),
            enabled = scanningEnabled
        ) {
            Text("Open scanner")
        }
    }
}
