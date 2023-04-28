package androidx.compose.samples.threednavi.network.geocodedata

data class GeocodeData(
    val candidates: List<Candidate?>?,
    val spatialReference: SpatialReference?
)