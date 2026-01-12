package de.htw_berlin.productscannerapp.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.htw_berlin.productscannerapp.ui.components.CategoryChip
import de.htw_berlin.productscannerapp.ui.components.SkeletonBlock
import de.htw_berlin.productscannerapp.ui.components.SkeletonCard
import android.content.Intent
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import de.htw_berlin.productscannerapp.ui.components.triadCategories
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale


@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    barcode: String,
    vm: ProductDetailViewModel = viewModel()
) {
    // load once per barcode
    LaunchedEffect(barcode) { vm.load(barcode) }

    val state by vm.state.collectAsState()

    when (state) {

        ProductDetailState.Loading -> {
            ProductDetailSkeleton(innerPadding = innerPadding)
        }

        is ProductDetailState.Error -> {
            val msg = (state as ProductDetailState.Error).message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Error: $msg")
            }
        }

        is ProductDetailState.Success -> {
            ProductDetailContent(
                innerPadding = innerPadding,
                state = (state as ProductDetailState.Success).data
            )
        }
    }
}

@Composable
private fun ProductDetailContent(
    innerPadding: PaddingValues,
    state: ProductDetailUiState
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(state.name, style = MaterialTheme.typography.headlineSmall)
            state.brand?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            val context = LocalContext.current
            val clipboard = LocalClipboardManager.current

            Card(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Barcode",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(state.barcode, style = MaterialTheme.typography.bodyLarge)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = {
                            clipboard.setText(AnnotatedString(state.barcode))
                            Toast.makeText(context, "Barcode copied", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy barcode")
                        }

                        IconButton(onClick = {
                            val shareText = buildString {
                                append("Product: ${state.name}\n")
                                state.brand?.let { append("Brand: $it\n") }
                                append("Barcode: ${state.barcode}")
                            }

                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share product"))
                        }) {
                            Icon(Icons.Outlined.Share, contentDescription = "Share")
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column {
                    if (!state.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = state.imageUrl,
                            contentDescription = "Product image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // simple fallback block if no image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            Text(
                                "No image available",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Extra fields
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        state.quantity?.let { Text("Quantity: $it") }
                        state.nutriScoreGrade?.let { Text("Nutri-Score: ${it.uppercase()}") }
                        state.offCategories?.let {
                            Text(
                                text = "Categories: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Text(
                    text = "Categories",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                val triad = triadCategories(state.categories)

                LazyRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(triad) { tag ->
                        CategoryChip(tag = tag, modifier = Modifier)
                    }
                }
            }
        }
        state.ingredients?.let { ing ->
            item {
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Ingredients",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = ing,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Text(
                    text = "Why this result?",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))

                if (state.reasons.isEmpty()) {
                    Text(
                        text = "No explanation available.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.reasons.forEachIndexed { index, reason ->
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (index != state.reasons.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
private fun ProductDetailSkeleton(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonBlock(Modifier.height(28.dp).fillMaxWidth(0.65f))
        SkeletonBlock(Modifier.height(18.dp).fillMaxWidth(0.4f))

        SkeletonCard {
            SkeletonBlock(Modifier.height(18.dp).fillMaxWidth(0.5f))
            Spacer(Modifier.height(12.dp))
            SkeletonBlock(Modifier.height(16.dp).fillMaxWidth())
        }

        SkeletonCard {
            SkeletonBlock(Modifier.height(18.dp).fillMaxWidth(0.35f))
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonBlock(Modifier.height(32.dp).width(90.dp))
                SkeletonBlock(Modifier.height(32.dp).width(90.dp))
                SkeletonBlock(Modifier.height(32.dp).width(90.dp))
            }
        }

        SkeletonCard {
            SkeletonBlock(Modifier.height(18.dp).fillMaxWidth(0.35f))
            Spacer(Modifier.height(12.dp))
            SkeletonBlock(Modifier.height(14.dp).fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            SkeletonBlock(Modifier.height(14.dp).fillMaxWidth(0.85f))
        }

        SkeletonCard {
            SkeletonBlock(Modifier.height(18.dp).fillMaxWidth(0.45f))
            Spacer(Modifier.height(12.dp))
            SkeletonBlock(Modifier.height(14.dp).fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            SkeletonBlock(Modifier.height(14.dp).fillMaxWidth(0.9f))
            Spacer(Modifier.height(8.dp))
            SkeletonBlock(Modifier.height(14.dp).fillMaxWidth(0.75f))
        }
    }
}
