package de.htw_berlin.productscannerapp.data.remote.off

import com.squareup.moshi.Json

data class OffProductResponse(
    @Json(name = "status") val status: Int?,
    @Json(name = "product") val product: OffProductDto?
)

data class OffProductDto(
    @Json(name = "product_name") val productName: String?,
    @Json(name = "brands") val brands: String?,
    @Json(name = "ingredients_text") val ingredientsText: String?,

    // ✅ Useful tags for classification + chips
    @Json(name = "ingredients_analysis_tags")
    val ingredientsAnalysisTags: List<String>?,

    @Json(name = "labels_tags")
    val labelsTags: List<String>?,

    @Json(name = "categories_tags")
    val categoriesTags: List<String>?,

    @Json(name = "allergens_tags")
    val allergensTags: List<String>?
)
