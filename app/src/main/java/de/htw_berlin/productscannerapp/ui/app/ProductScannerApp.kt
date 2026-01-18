package de.htw_berlin.productscannerapp.ui.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import android.os.Build
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScannerApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route

    val isTopLevel = currentRoute in setOf(
        AppRoute.Scan.route,
        AppRoute.History.route,
        AppRoute.Settings.route,
        AppRoute.About.route
    )

    val showUp = !isTopLevel && navController.previousBackStackEntry != null
    val context = LocalContext.current

    val title = when (currentRoute) {
        AppRoute.Scan.route -> "Scanner"
        AppRoute.History.route -> "History"
        AppRoute.Settings.route -> "Settings"
        AppRoute.About.route -> "About"
        else -> "Product"
    }

    // treat opening/closing as open -> smooth blur transitions
    val drawerOpen by remember {
        derivedStateOf {
            drawerState.currentValue == DrawerValue.Open || drawerState.targetValue == DrawerValue.Open
        }
    }

    // Blur behind drawer (Android 12+). On Android 10 it won't blur — scrim will handle readability.
    val blurModifier = if (drawerOpen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.blur(
            radius = 22.dp,
            edgeTreatment = BlurredEdgeTreatment.Rectangle
        )
    } else {
        Modifier
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // stronger dim so text stays readable (especially Android 10 where blur won't apply)
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.60f),
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                drawerTonalElevation = 0.dp,
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppDrawer(
                        selectedRoute = currentRoute,
                        onNavigate = { route ->
                            scope.launch { drawerState.close() }
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        // Blur ONLY app content behind drawer
        Box(modifier = Modifier.fillMaxSize().then(blurModifier)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.40f)
                        ),
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
                    startDestination = AppRoute.Scan.route
                ) {
                    composable(AppRoute.Scan.route) {
                        ScanRoute(
                            innerPadding = innerPadding,
                            context = context,
                            onBarcode = { code ->
                                val normalized = code.trim().filter(Char::isDigit)
                                navController.navigate(AppRoute.ProductDetail.createRoute(normalized))
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
                    composable(AppRoute.Settings.route) { SettingsScreen(innerPadding) }
                    composable(AppRoute.About.route) { AboutScreen(innerPadding) }

                    composable(
                        route = AppRoute.ProductDetail.route,
                        arguments = listOf(navArgument("barcode") { type = NavType.StringType })
                    ) { entry ->
                        val barcode = entry.arguments?.getString("barcode").orEmpty()
                        ProductDetailScreen(innerPadding = innerPadding, barcode = barcode)
                    }
                }
            }
        }
    }
}