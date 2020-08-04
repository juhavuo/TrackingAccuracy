package fi.metropolia.juhavuo.trackingaccuracy

import org.osmdroid.util.GeoPoint

data class MappedRouteJson(
    val type: Int, // 0 original, 1 pecker, 2
    val parameter_Data: String?,
    val geoPoints: ArrayList<GeoPoint>
)