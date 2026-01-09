package de.htw_berlin.productscannerapp.ui.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    selectedRoute: String?,
    onNavigate: (String) -> Unit,
    contentPadding: PaddingValues = PaddingValues(12.dp),
) {
    ModalDrawerSheet(
        drawerContainerColor = DrawerDefaults.containerColor
    ) {
        Text(
            text = "Product Scanner",
            modifier = Modifier.padding(16.dp),
        )

        drawerItems.forEach { item ->
            val isSelected = selectedRoute == item.route.route
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onNavigate(item.route.route) },
                icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors()
            )
        }
    }
}
