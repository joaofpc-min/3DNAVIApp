package androidx.compose.samples.threednavi.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val productName: String,
    @ColumnInfo(name = "image")
    val imageUrl: String
)
