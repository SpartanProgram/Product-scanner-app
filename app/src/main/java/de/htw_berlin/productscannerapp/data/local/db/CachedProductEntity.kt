package de.htw_berlin.productscannerapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class CachedProductEntity(
    @PrimaryKey val barcode: String,
    val name: String,
    val brand: String?,
    val ingredients: String?,
    val updatedAtMillis: Long
)
