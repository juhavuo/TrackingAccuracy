package fi.metropolia.juhavuo.trackingaccuracy

import org.osmdroid.util.GeoPoint

//for saving route information to Json
data class MappedRouteJson(
    val type: Int, // 0 original, 1 pecker, 2
    val parameter_Data: String?,
    val geoPoints: ArrayList<GeoPoint>
)