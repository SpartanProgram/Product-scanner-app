package de.htw_berlin.productscannerapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val barcode: String,
    val name: String,
    val brand: String?,
    val ingredients: String?,
    val categoriesCsv: String, // 3 enums CSV
    val reasonsText: String,   // store reasons as text (newline-separated)

    val imageUrl: String?,
    val quantity: String?,
    val nutriScoreGrade: String?,
    val offCategories: String?,

    val updatedAtMillis: Long

)