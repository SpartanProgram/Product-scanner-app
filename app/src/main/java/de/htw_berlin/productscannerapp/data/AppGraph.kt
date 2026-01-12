package de.htw_berlin.productscannerapp.data

import android.content.Context
import de.htw_berlin.productscannerapp.data.local.db.ProductScannerDatabase
import de.htw_berlin.productscannerapp.data.remote.NetworkModule
import de.htw_berlin.productscannerapp.data.repository.*
import java.util.concurrent.atomic.AtomicBoolean

object AppGraph {

    private val initialized = AtomicBoolean(false)

    lateinit var productRepository: ProductRepository
        private set

    val historyRepository: HistoryRepository = InMemoryHistoryRepository()

    fun init(context: Context) {
        if (initialized.getAndSet(true)) return

        val db = ProductScannerDatabase.getInstance(context)
        val dao = db.productDao()
        val api = NetworkModule.openFoodFactsApi

        productRepository = OpenFoodFactsProductRepository(api, dao)
    }
}
