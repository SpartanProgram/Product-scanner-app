package de.htw_berlin.productscannerapp.data.remote.off

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProduct(
        @Path("barcode") barcode: String,
        @Query("lc") lc: String = "de"
    ): OffProductResponse
}
