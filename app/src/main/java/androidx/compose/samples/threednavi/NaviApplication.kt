package androidx.compose.samples.threednavi

import android.app.Application
import androidx.compose.samples.threednavi.data.MarketRoomDatabase
import androidx.compose.samples.threednavi.data.OrderPriceRoomDatabase
import androidx.compose.samples.threednavi.data.ProductRoomDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NaviApplication : Application() {
    val productdatabase: ProductRoomDatabase by lazy { ProductRoomDatabase.getDatabase(this) }
    val marketdatabase: MarketRoomDatabase by lazy { MarketRoomDatabase.getDatabase(this) }
    val orderpricedatabase: OrderPriceRoomDatabase by lazy { OrderPriceRoomDatabase.getDatabase(this) }

    //val repository: MainRepository by lazy { MainRepository(productdatabase.productDao(), marketdatabase.marketDao()) }
}
