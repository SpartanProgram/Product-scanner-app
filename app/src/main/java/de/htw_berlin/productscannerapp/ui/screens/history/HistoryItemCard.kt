package de.htw_berlin.productscannerapp.ui.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.htw_berlin.productscannerapp.ui.components.CategoryChip
import de.htw_berlin.productscannerapp.ui.components.triadCategories

@Composable
fun HistoryItemCard(
    item: HistoryItemUi,
    onClick: (String) -> Unit
) {
    val shape = RoundedCornerShape(22.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable { onClick(item.barcode) },
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row: thumbnail + title/brand + quick facts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Thumbnail(imageUrl = item.imageUrl)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2
                    )
                    item.brand?.takeIf { it.isNotBlank() }?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Barcode: ${item.barcode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    // Quick facts chips
                    val qty = item.quantity?.takeIf { it.isNotBlank() }
                    val ns = item.nutriScoreGrade?.takeIf { it.isNotBlank() }?.uppercase()

                    if (qty != null) {
                        AssistChip(onClick = {}, label = { Text(qty) })
                        Spacer(Modifier.height(6.dp))
                    }
                    if (ns != null) {
                        AssistChip(onClick = {}, label = { Text("Nutri-Score $ns") })
                    }
                }
            }

            // Category triad row (your 3 chips)
            val triad = triadCategories(item.categories)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(triad) { tag ->
                    CategoryChip(tag = tag)
                }
            }
        }
    }
}

@Composable
private fun Thumbnail(imageUrl: String?) {
    val thumbShape = RoundedCornerShape(16.dp)

    Surface(
        modifier = Modifier
            .size(64.dp)
            .clip(thumbShape),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f),
        shape = thumbShape
    ) {
        val url = imageUrl?.takeIf { it.isNotBlank() }

        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = "Product image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
