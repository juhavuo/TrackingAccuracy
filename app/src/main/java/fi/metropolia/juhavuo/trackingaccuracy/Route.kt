package fi.metropolia.juhavuo.trackingaccuracy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(
    @PrimaryKey
    val routeid: Int,
    var name: String,
    var description: String,
    val startingTime: Long,
    var stoppingTime: Long
){

    override fun toString(): String {

        var returnString = "Route: $name\n Description: $description\n Starts: $startingTime"

        if(stoppingTime == -1L){
            returnString+=", ongoing"
        }else{
            returnString+=", stops: $stoppingTime"
        }

        return returnString
    }
}