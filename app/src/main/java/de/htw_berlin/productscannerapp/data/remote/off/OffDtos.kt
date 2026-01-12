package de.htw_berlin.productscannerapp.data.remote.off

import com.squareup.moshi.Json

data class OffProductResponse(
    @Json(name = "status") val status: Int?,
    @Json(name = "product") val product: OffProductDto?
)

data class OffProductDto(
    @Json(name = "product_name") val productName: String?,
    @Json(name = "brands") val brands: String?,
    @Json(name = "ingredients_text") val ingredientsText: String?
)
