package androidx.compose.samples.threednavi.network.geocodedata

data class Candidate(
    val address: String?,
    val attributes: Attributes?,
    val extent: Extent?,
    val location: Location?,
    val score: Double?
)