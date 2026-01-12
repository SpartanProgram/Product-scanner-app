package de.htw_berlin.productscannerapp.data.remote.off

import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {

    // Official common read endpoint:
    // https://world.openfoodfacts.org/api/v0/product/{barcode}.json :contentReference[oaicite:1]{index=1}
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): OffProductResponse
}
