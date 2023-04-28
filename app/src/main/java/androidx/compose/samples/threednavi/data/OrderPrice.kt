package androidx.compose.samples.threednavi.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orderVsPrice")
data class OrderPrice(
    @PrimaryKey
    @ColumnInfo(name = "ordername")
    val orderName: String,
    @ColumnInfo(name = "storename")
    val storeName: String,
    @ColumnInfo(name = "price")
    val price: Double
)
/*
fun Product.getFormattedPrice(): androidx.compose.samples.crane.network.directionsdata.Estringue =
    NumberFormat.getCurrencyInstance().format(productPrice)
*/