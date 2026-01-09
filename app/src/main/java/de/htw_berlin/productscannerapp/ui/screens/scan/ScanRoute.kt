package de.htw_berlin.productscannerapp.ui.screens.scan

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
import android.content.Context

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

@Composable
fun ScanRoute(
    innerPadding: PaddingValues,
    context: Context,
    onBarcode: (String) -> Unit
) {
    val scanner = remember { provideScanner(context) }
    val scope = rememberCoroutineScope()

    ScanScreen(
        innerPadding = innerPadding,
        onStartScan = {
            scope.launch {
                try {
                    val result = scanner.startScan().await()
                    val code = result.rawValue
                    if (!code.isNullOrBlank()) onBarcode(code)
                } catch (_: Exception) {
                    // user cancelled / scanner failed -> ignore for now
                }
            }
        },
        onBarcodeEntered = { code ->
            onBarcode(code)
        }
    )
}
