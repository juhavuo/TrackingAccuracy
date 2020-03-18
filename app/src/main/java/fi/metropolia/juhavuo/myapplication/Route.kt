package fi.metropolia.juhavuo.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(
    @PrimaryKey
    val routeid: Int,
    val name: String,
    val description: String,
    val startingTime: Long,
    val stoppingTime: Long
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