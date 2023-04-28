package androidx.compose.samples.threednavi.network.directionsdata

data class DirectionPoints(
    val features: List<FeatureX>?,
    val fieldAliases: FieldAliasesX?,
    val geometryType: String?,
    val spatialReference: SpatialReference?
)