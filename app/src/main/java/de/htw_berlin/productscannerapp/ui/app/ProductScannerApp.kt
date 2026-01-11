package de.htw_berlin.productscannerapp.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.htw_berlin.productscannerapp.ui.screens.about.AboutScreen
import de.htw_berlin.productscannerapp.ui.screens.detail.ProductDetailScreen
import de.htw_berlin.productscannerapp.ui.screens.history.HistoryScreen
import de.htw_berlin.productscannerapp.ui.screens.scan.ScanRoute
import de.htw_berlin.productscannerapp.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch

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
                composable(AppRoute.Scan.route) {
                    ScanRoute(
                        innerPadding = innerPadding,
                        context = context,
                        onBarcode = { code ->
                            navController.navigate(AppRoute.ProductDetail.createRoute(code))
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

                // declare barcode argument for reliability
                composable(
                    route = AppRoute.ProductDetail.route,
                    arguments = listOf(navArgument("barcode") { type = NavType.StringType })
                ) { entry ->
                    val barcode = entry.arguments?.getString("barcode").orEmpty()
                    ProductDetailScreen(
                        innerPadding = innerPadding,
                        barcode = barcode
                    )
                }
            }
        }
    }
}
