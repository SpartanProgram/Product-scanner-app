package de.htw_berlin.productscannerapp.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(val route: String) {
    data object Scan : AppRoute("scan")
    data object History : AppRoute("history")
    data object Settings : AppRoute("settings")
    data object About : AppRoute("about")

    data object ProductDetail : AppRoute("detail/{barcode}") {
        fun createRoute(barcode: String) = "detail/$barcode"
    }
}


data class DrawerItem(
    val route: AppRoute,
    val label: String,
    val icon: ImageVector
)

val drawerItems = listOf(
    DrawerItem(AppRoute.Scan, "Scanner", Icons.Outlined.QrCodeScanner),
    DrawerItem(AppRoute.History, "History", Icons.Outlined.History),
    DrawerItem(AppRoute.Settings, "Settings", Icons.Outlined.Settings),
    DrawerItem(AppRoute.About, "About", Icons.Outlined.Info),
)
