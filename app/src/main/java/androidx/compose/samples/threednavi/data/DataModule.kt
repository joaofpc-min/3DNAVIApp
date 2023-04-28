package androidx.compose.samples.threednavi.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    //----------------------------Product Database
    @Provides
    @Singleton
    fun provideProductDatabase(@ApplicationContext context: Context): ProductRoomDatabase =
        ProductRoomDatabase.getDatabase(context)

    @Provides
    fun provideProductDao(database: ProductRoomDatabase): ProductDao {
        return database.productDao()
    }

    //---------------------------Market Database
    @Provides
    @Singleton
    fun provideMarketDatabase(@ApplicationContext context: Context): MarketRoomDatabase =
        MarketRoomDatabase.getDatabase(context)

    @Provides
    fun provideMarkettDao(database: MarketRoomDatabase): MarketDao {
        return database.marketDao()
    }

    //---------------------------OrderPrice Database
    @Provides
    @Singleton
    fun provideOrderPriceDatabase(@ApplicationContext context: Context): OrderPriceRoomDatabase =
        OrderPriceRoomDatabase.getDatabase(context)

    @Provides
    fun provideOrderPriceDao(database: OrderPriceRoomDatabase): OrderPriceDao {
        return database.orderpriceDao()
    }

}