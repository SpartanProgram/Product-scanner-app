package de.htw_berlin.productscannerapp.ui.screens.scan

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private fun provideScanner(context: Context): GmsBarcodeScanner {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E
        )
        .enableAutoZoom()
        .build()

    return GmsBarcodeScanning.getClient(context, options)
}

private fun normalizeBarcode(input: String): String =
    input.trim().filter(Char::isDigit) // removes spaces, dashes, etc.

private fun isSupportedBarcodeLength(digits: String): Boolean =
    digits.length == 8 || digits.length == 12 || digits.length == 13 || digits.length == 14

@Composable
fun ScanRoute(
    innerPadding: PaddingValues,
    context: Context,
    onBarcode: (String) -> Unit
) {
    // IMPORTANT: tie remember to context to avoid stale Activity reference after recreation
    val scanner = remember(context) { provideScanner(context) }
    val scope = rememberCoroutineScope()

    fun submit(raw: String) {
        val normalized = normalizeBarcode(raw)

        if (normalized.isBlank()) {
            Toast.makeText(context, "Please enter a barcode.", Toast.LENGTH_SHORT).show()
            return
        }

        // Optional but recommended: prevents navigating to garbage input
        if (!isSupportedBarcodeLength(normalized)) {
            Toast.makeText(context, "Invalid barcode length: ${normalized.length}", Toast.LENGTH_SHORT).show()
            return
        }

        onBarcode(normalized)
    }

    ScanScreen(
        innerPadding = innerPadding,
        onStartScan = {
            scope.launch {
                try {
                    val result = scanner.startScan().await()
                    val code = result.rawValue
                    if (!code.isNullOrBlank()) submit(code)
                } catch (_: Exception) {
                    // cancelled / failed -> ignore (optional: show a snackbar)
                }
            }
        },
        onBarcodeEntered = { typed ->
            submit(typed)
        }
    )
}
