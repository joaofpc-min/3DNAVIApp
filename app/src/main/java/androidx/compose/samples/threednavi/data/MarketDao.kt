package androidx.compose.samples.threednavi.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(market: Market)
    @Update
    suspend fun update(market: Market)
    @Delete
    suspend fun delete(market: Market)
    @Query("SELECT * from market WHERE name = :name")
    fun getMarket(name: String): Flow<Market>
    @Query("SELECT * from market ORDER BY name ASC")
    fun getMarkets(): Flow<List<Market>>
}