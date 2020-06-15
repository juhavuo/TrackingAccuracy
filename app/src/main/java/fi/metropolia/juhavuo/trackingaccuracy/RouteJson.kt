package fi.metropolia.juhavuo.trackingaccuracy

data class RouteJson(
    val route: Route,
    val locations: ArrayList<MeasuredLocation>
)