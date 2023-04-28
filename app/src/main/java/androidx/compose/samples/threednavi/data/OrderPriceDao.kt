package androidx.compose.samples.threednavi.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderPrice(orderPrice: OrderPrice)
    @Update
    suspend fun updateOrderPrice(orderPrice: OrderPrice)
    @Query("DELETE FROM orderVsPrice WHERE ordername = :order")
    suspend fun deleteOrderPrice(order: String)
    @Query("SELECT * from orderVsPrice WHERE ordername = :order")
    fun getOrderPrice(order: String): Flow<OrderPrice>
    @Query("SELECT * from orderVsPrice ORDER BY price ASC")
    fun getOrderPrices(): Flow<List<OrderPrice>>
}
