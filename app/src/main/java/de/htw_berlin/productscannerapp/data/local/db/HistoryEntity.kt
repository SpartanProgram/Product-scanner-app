package de.htw_berlin.productscannerapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val barcode: String,
    val name: String,
    val brand: String?,
    val ingredients: String?,
    val categoriesCsv: String, // store enums as CSV (simple)
    val updatedAtMillis: Long
)
