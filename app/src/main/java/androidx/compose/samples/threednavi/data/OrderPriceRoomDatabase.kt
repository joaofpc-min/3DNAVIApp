package androidx.compose.samples.threednavi.data

import android.content.Context
import androidx.room.*

@Database(entities = [OrderPrice::class], version = 1 , exportSchema = false)
abstract class OrderPriceRoomDatabase : RoomDatabase() {
    abstract fun orderpriceDao():OrderPriceDao

    companion object {
        @Volatile
        private var INSTANCE: OrderPriceRoomDatabase? = null
        fun getDatabase(context: Context): OrderPriceRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    OrderPriceRoomDatabase::class.java,
                    "order_price_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
