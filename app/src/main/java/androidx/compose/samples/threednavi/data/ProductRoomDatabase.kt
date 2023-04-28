package androidx.compose.samples.threednavi.data

import android.content.Context
import androidx.room.*


@Database(entities = [Product::class], version = 1 , exportSchema = false)
@TypeConverters(PriceTypeConverter::class)
abstract class ProductRoomDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: ProductRoomDatabase? = null
        fun getDatabase(context: Context): ProductRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    ProductRoomDatabase::class.java,
                    "product_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}

object PriceTypeConverter {
    @TypeConverter
    fun fromListDoubleToString(doubleList: List<Double?>): String = doubleList.toString()
    @TypeConverter
    fun toListDoubleFromString(stringList: String): List<Double?> {
        val result = ArrayList<Double?>()
        val split =stringList.replace("[","").replace("]","").replace(" ","").split(",")
        for (n in split) {
            try {
                result.add(n.toDouble())
            } catch (e: Exception) {

            }
        }
        return result
    }
}

/*
object PriceTypeConverter {

    @TypeConverter
    @JvmStatic
    fun priceToString(prices: MutableList<androidx.compose.samples.crane.network.directionsdata.Estringue>?): androidx.compose.samples.crane.network.directionsdata.Estringue? {
        return prices?.map { it }?.joinToString()
    }

    @TypeConverter
    @JvmStatic
    fun stringToPrices(prices: androidx.compose.samples.crane.network.directionsdata.Estringue?): MutableList<androidx.compose.samples.crane.network.directionsdata.Estringue>? {
        return prices?.split(" ")?.map { prices }?.toMutableList()
    }

}
*/