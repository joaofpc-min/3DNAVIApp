package androidx.compose.samples.threednavi.network.directionsdata

data class DirectionsData(
    val checksum: String?,
    val directionLines: DirectionLines?,
    val directionPoints: DirectionPoints?,
    val messages: List<Any>?,
    val requestID: String?,
    val routes: Routes?
)