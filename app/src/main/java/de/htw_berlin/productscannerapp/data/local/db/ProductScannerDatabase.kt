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
    version = 4,
    exportSchema = false
)
abstract class ProductScannerDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: ProductScannerDatabase? = null

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE history ADD COLUMN imageUrl TEXT")
                db.execSQL("ALTER TABLE history ADD COLUMN quantity TEXT")
                db.execSQL("ALTER TABLE history ADD COLUMN nutriScoreGrade TEXT")
                db.execSQL("ALTER TABLE history ADD COLUMN offCategories TEXT")
            }
        }

        fun getInstance(context: Context): ProductScannerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ProductScannerDatabase::class.java,
                    "product_scanner_db"
                )
                    .addMigrations(MIGRATION_3_4)
                    // you can keep this while developing
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
