package androidx.compose.samples.threednavi.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market")
data class Market(
    @PrimaryKey
    @ColumnInfo(name = "name")
    val marketName: String,
    @ColumnInfo(name = "site")
    val url: String,
    @ColumnInfo(name = "distance")
    val distance: Double
)
