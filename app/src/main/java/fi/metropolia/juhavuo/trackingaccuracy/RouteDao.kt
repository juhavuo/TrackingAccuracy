package fi.metropolia.juhavuo.trackingaccuracy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RouteDao{

    @Query("SELECT * FROM route")
    fun getRoutes(): List<Route>

    @Query("SELECT * FROM route WHERE routeid =:id")
    fun getRouteWithId(id: Int): Route

    //get the id of the newest route
    @Query("SELECT MAX(routeid) FROM route")
    fun getBiggestRouteId(): Int

    //get amount of routes
    @Query("SELECT COUNT(*) FROM route")
    fun getAmountOfRoutes(): Int

    //insert the route data
    @Insert
    fun insert(route: Route)

    //update stopping time
    @Query("UPDATE route SET stoppingTime =:stoppingTime WHERE routeid =:id")
    fun updateStoppingtime(id: Int, stoppingTime: Long)

    @Query("DELETE FROM route WHERE routeid =:id")
    fun deleteRouteWithId(id: Int)

    @Query("UPDATE route SET epsilon =:epsilon WHERE routeid =:id")
    fun updateEpsilonValue(epsilon: Double, id: Int)

    @Query("UPDATE route SET accuracyThreshold =:accuracyThreshold WHERE routeid =:id")
    fun updateAccuracyTreshold(accuracyThreshold: Int, id: Int)
}