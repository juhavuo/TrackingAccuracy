package fi.metropolia.juhavuo.trackingaccuracy

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("routeID")],
   foreignKeys = [ForeignKey(
      onDelete = ForeignKey.CASCADE,
      entity = Route::class,
      parentColumns = ["routeid"],
      childColumns = ["routeID"]
   )]
)
data class MeasuredLocation(
   @PrimaryKey
   val locationID: Int,
   val routeID: Int,
   val latitude: Double,
   val longitude: Double,
   val altitude: Double,
   val speed: Float,
   val accuracy: Float,
   val timestamp: Long
){
   override fun toString(): String = "measured location: lat $latitude, lng $longitude, alt $altitude, speed: $speed, acc $accuracy, timestamp $timestamp"

}