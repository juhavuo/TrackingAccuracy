package fi.metropolia.juhavuo.trackingaccuracy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MeasuredLocationDao{

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(measuredLocation: MeasuredLocation)

    @Query("SELECT * FROM measuredlocation WHERE routeID =:routeId")
    fun getLocationsOfRouteWithId(routeId: Int): List<MeasuredLocation>

    //if deleting route leads to deleting locations related to route
    //this might be useless
    /*
    @Query("DELETE FROM measuredlocation WHERE routeID =:routeId")
    fun deleteLocationsOfRouteWithId(routeId: Int)*/

}