import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import de.htw_berlin.productscannerapp.ui.app.ProductScannerApp
import de.htw_berlin.productscannerapp.ui.theme.ProductScannerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductScannerAppTheme {
                ProductScannerApp()
            }
        }
    }
}
