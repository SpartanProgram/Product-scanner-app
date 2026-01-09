package de.htw_berlin.productscannerapp.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.htw_berlin.productscannerapp.ui.screens.about.AboutScreen
import de.htw_berlin.productscannerapp.ui.screens.detail.ProductDetailScreen
import de.htw_berlin.productscannerapp.ui.screens.history.HistoryScreen
import de.htw_berlin.productscannerapp.ui.screens.scan.ScanScreen
import de.htw_berlin.productscannerapp.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import de.htw_berlin.productscannerapp.ui.screens.scan.ScanRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScannerApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentDestination = backStackEntry?.destination
    val currentRoute = currentDestination?.route

    val isTopLevel = drawerItems.any { it.route.route == currentRoute }
    val showUp = !isTopLevel && navController.previousBackStackEntry != null
    val context = LocalContext.current

    val title = when (currentRoute) {
        AppRoute.Scan.route -> "Scanner"
        AppRoute.History.route -> "History"
        AppRoute.Settings.route -> "Settings"
        AppRoute.About.route -> "About"
        else -> "Product"
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                selectedRoute = currentRoute,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    navController.navigate(route) {
                        // avoids building a huge backstack when switching drawer items
                        popUpTo(AppRoute.Scan.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { androidx.compose.material3.Text(title) },
                    navigationIcon = {
                        if (showUp) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Outlined.Menu, contentDescription = "Menu")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppRoute.Scan.route,
                modifier = Modifier
            ) {
                composable("scan") {
                    ScanRoute(
                        innerPadding = innerPadding,
                        context = context,
                        onBarcode = { code ->
                            navController.navigate("detail/$code")
                        }
                    )
                }

                composable(AppRoute.History.route) {
                    HistoryScreen(
                        innerPadding = innerPadding,
                        onOpenItem = { barcode ->
                            navController.navigate(AppRoute.ProductDetail.createRoute(barcode))
                        }
                    )
                }

                composable(AppRoute.Settings.route) {
                    SettingsScreen(innerPadding = innerPadding)
                }

                composable(AppRoute.About.route) {
                    AboutScreen(innerPadding = innerPadding)
                }

                composable(AppRoute.ProductDetail.route) { backStackEntry ->
                    val barcode = backStackEntry.arguments?.getString("barcode").orEmpty()
                    ProductDetailScreen(
                        innerPadding = innerPadding,
                        barcode = barcode
                    )
                }
            }
        }
    }
}
