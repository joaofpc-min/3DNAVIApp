package androidx.compose.samples.threednavi.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)
    @Update
    suspend fun update(product: Product)
    @Query("DELETE FROM product WHERE name = :order")
    suspend fun delete(order: String)
    @Query("SELECT * from product WHERE name = :name")
    fun getItem(name: String): Flow<Product>
    @Query("SELECT * from product ORDER BY name ASC")
    fun getItems(): Flow<List<Product>>
}
