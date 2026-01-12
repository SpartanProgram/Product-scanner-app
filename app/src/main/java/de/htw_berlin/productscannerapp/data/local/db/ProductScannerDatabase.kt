package de.htw_berlin.productscannerapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ProductScannerDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

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
