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

    @Query("SELECT MAX(locationID) FROM measuredlocation")
    fun getLargestLocationId(): Int

    @Query("SELECT COUNT(*) FROM measuredLocation")
    fun getAmountOfLocations(): Int

    @Query("SELECT * FROM measuredlocation")
    fun getAllLocations(): List<MeasuredLocation>

    //if deleting route leads to deleting locations related to route
    //this might be useless
    /*
    @Query("DELETE FROM measuredlocation WHERE routeID =:routeId")
    fun deleteLocationsOfRouteWithId(routeId: Int)*/

}