package de.htw_berlin.productscannerapp.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.htw_berlin.productscannerapp.data.remote.off.OpenFoodFactsApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://world.openfoodfacts.org/"

    private val userAgentInterceptor = Interceptor { chain ->
        val req = chain.request().newBuilder()
            .header("User-Agent", "ProductScannerApp/1.0 (HTW Berlin; Android)")
            .build()
        chain.proceed(req)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(userAgentInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    val openFoodFactsApi: OpenFoodFactsApi = retrofit.create(OpenFoodFactsApi::class.java)
}
