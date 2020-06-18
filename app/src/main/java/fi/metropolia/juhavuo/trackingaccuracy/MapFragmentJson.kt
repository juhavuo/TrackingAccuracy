package fi.metropolia.juhavuo.trackingaccuracy

data class MapFragmentJson(
    val route_id: Int,
    val route_name: String,
    val mappedRoutes: ArrayList<MappedRouteJson>
)