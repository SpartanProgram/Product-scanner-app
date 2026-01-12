package de.htw_berlin.productscannerapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedProductEntity::class, HistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ProductScannerDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun historyDao(): HistoryDao
    companion object {
        @Volatile private var INSTANCE: ProductScannerDatabase? = null

        fun getInstance(context: Context): ProductScannerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProductScannerDatabase::class.java,
                    "product_scanner_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
