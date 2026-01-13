// data/local/db/ProductScannerDatabase.kt
package de.htw_berlin.productscannerapp.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CachedProductEntity::class, HistoryEntity::class],
    version = 3, // bump
    exportSchema = false
)
abstract class ProductScannerDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: ProductScannerDatabase? = null

        // Migration 2 -> 3: add reasonsText column
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE history ADD COLUMN reasonsText TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        fun getInstance(context: Context): ProductScannerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProductScannerDatabase::class.java,
                    "product_scanner_db"
                )
                    .addMigrations(MIGRATION_2_3)
                    // keep this if you want safety in dev:
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
