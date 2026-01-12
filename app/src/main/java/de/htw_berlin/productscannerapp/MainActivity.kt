package de.htw_berlin.productscannerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.htw_berlin.productscannerapp.ui.app.ProductScannerApp
import de.htw_berlin.productscannerapp.ui.theme.ProductScannerAppTheme
import de.htw_berlin.productscannerapp.data.AppGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppGraph.init(applicationContext)

        setContent {
            ProductScannerAppTheme {
                ProductScannerApp()
            }
        }
    }
}
