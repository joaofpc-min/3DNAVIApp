package androidx.compose.samples.threednavi.network.directionsdata

data class DirectionLines(
    val features: List<Feature?>?,
    val fieldAliases: FieldAliases?,
    val geometryType: String?,
    val spatialReference: SpatialReference?
)