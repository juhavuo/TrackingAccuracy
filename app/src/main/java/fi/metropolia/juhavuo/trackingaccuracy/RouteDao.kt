package fi.metropolia.juhavuo.trackingaccuracy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RouteDao{

    //get the id of the newest route
    @Query("SELECT MAX(routeid) FROM route")
    fun getBiggestRouteId(): Int

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


}