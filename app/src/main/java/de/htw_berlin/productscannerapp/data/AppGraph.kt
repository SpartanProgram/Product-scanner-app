package de.htw_berlin.productscannerapp.data

import android.content.Context
import de.htw_berlin.productscannerapp.data.local.db.ProductScannerDatabase
import de.htw_berlin.productscannerapp.data.remote.NetworkModule
import de.htw_berlin.productscannerapp.data.repository.HistoryRepository
import de.htw_berlin.productscannerapp.data.repository.OpenFoodFactsProductRepository
import de.htw_berlin.productscannerapp.data.repository.ProductRepository
import de.htw_berlin.productscannerapp.data.repository.RoomHistoryRepository
import java.util.concurrent.atomic.AtomicBoolean

object AppGraph {

    private val initialized = AtomicBoolean(false)

    lateinit var productRepository: ProductRepository
        private set

    lateinit var historyRepository: HistoryRepository
        private set

    fun init(context: Context) {
        if (initialized.getAndSet(true)) return

        val db = ProductScannerDatabase.getInstance(context)

        // OFF repo (network + cache)
        productRepository = OpenFoodFactsProductRepository(
            api = NetworkModule.openFoodFactsApi,
            dao = db.productDao()
        )

        // ✅ Room-backed history
        historyRepository = RoomHistoryRepository(db.historyDao())
    }
}
