package de.htw_berlin.productscannerapp.ui.screens.detail

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import de.htw_berlin.productscannerapp.ui.components.CategoryChip
import de.htw_berlin.productscannerapp.ui.components.CollapsibleCard
import de.htw_berlin.productscannerapp.ui.components.GlassCard
import de.htw_berlin.productscannerapp.ui.components.SkeletonBlock
import de.htw_berlin.productscannerapp.ui.components.SkeletonCard
import de.htw_berlin.productscannerapp.ui.components.triadCategories
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.lerp
import kotlin.math.min
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    barcode: String,
    vm: ProductDetailViewModel = viewModel()
) {
    LaunchedEffect(barcode) { vm.load(barcode) }
    val state by vm.state.collectAsState()

    Crossfade(
        targetState = state,
        label = "detail_crossfade"
    ) { s ->
        when (s) {
            ProductDetailState.Loading -> ProductDetailSkeleton(innerPadding)

            is ProductDetailState.Error -> {
                val msg = s.message
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) { Text("Error: $msg") }
            }

            is ProductDetailState.Success -> {
                ProductDetailContent(
                    innerPadding = innerPadding,
                    state = s.data
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductDetailContent(
    innerPadding: PaddingValues,
    state: ProductDetailUiState
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val listState = rememberLazyListState()

    val qty = state.quantity?.takeIf { it.isNotBlank() }
    val ns = state.nutriScoreGrade?.takeIf { it.isNotBlank() }?.uppercase()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1) Collapsing hero
        item {
            CollapsingHeroHeader(state = state, listState = listState)
        }

        // 2) Sticky header (your 3 category chips always visible after hero)
        stickyHeader {
            val triad = triadCategories(state.categories)

            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.86f),
                tonalElevation = 1.dp,
                shadowElevation = 2.dp
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(triad) { tag ->
                        CategoryChip(tag = tag)
                    }
                }
            }
        }

        // 3) Rest of your content
        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Barcode card + actions
                GlassCard(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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

                // Quick facts
                if (qty != null || ns != null || !state.offCategories.isNullOrBlank()) {
                    GlassCard(Modifier.fillMaxWidth()) {
                        Text("Quick facts", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(10.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            qty?.let { item { AssistChip(onClick = {}, label = { Text(it) }) } }
                            ns?.let { item { AssistChip(onClick = {}, label = { Text("Nutri-Score $it") }) } }
                            state.offCategories?.takeIf { it.isNotBlank() }?.let { cats ->
                                item { AssistChip(onClick = {}, label = { Text(cats) }) }
                            }
                        }
                    }
                }

                // Collapsibles (your existing ones are fine)
                CollapsibleCard(
                    title = "Ingredients",
                    subtitle = state.ingredients?.takeIf { it.isNotBlank() } ?: "No ingredients listed",
                    initiallyExpanded = false
                ) {
                    Text(
                        text = state.ingredients?.takeIf { it.isNotBlank() } ?: "No ingredients available.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                CollapsibleCard(
                    title = "How we decided",
                    subtitle = if (state.reasons.isEmpty()) "No explanation available" else "${state.reasons.size} signals found",
                    initiallyExpanded = true
                ) {
                    if (state.reasons.isEmpty()) {
                        Text(
                            "No explanation available.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        state.reasons.forEachIndexed { index, reason ->
                            Text("• $reason", style = MaterialTheme.typography.bodyMedium)
                            if (index != state.reasons.lastIndex) Spacer(Modifier.height(6.dp))
                        }
                    }
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
        SkeletonBlock(Modifier.height(220.dp).fillMaxWidth())
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
    }
}

@Composable
private fun CollapsingHeroHeader(
    state: ProductDetailUiState,
    listState: LazyListState,
    expandedHeight: Dp = 260.dp,
    collapsedHeight: Dp = 92.dp
) {
    val density = LocalDensity.current
    val collapseRangePx = with(density) { (expandedHeight - collapsedHeight).toPx() }

    val scrollPx by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                min(listState.firstVisibleItemScrollOffset.toFloat(), collapseRangePx)
            } else {
                collapseRangePx
            }
        }
    }

    val fraction = (scrollPx / collapseRangePx).coerceIn(0f, 1f)
    val headerHeight = lerp(expandedHeight, collapsedHeight, fraction)

    val bigTitleAlpha = (1f - fraction * 1.2f).coerceIn(0f, 1f)
    val smallTitleAlpha = (fraction * 1.2f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        val url = state.imageUrl?.takeIf { it.isNotBlank() }

        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = "Product image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.05f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
                            )
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = state.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.graphicsLayer { alpha = bigTitleAlpha }
            )
            state.brand?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.graphicsLayer { alpha = bigTitleAlpha }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = state.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                modifier = Modifier.graphicsLayer { alpha = smallTitleAlpha }
            )
        }
    }
}

