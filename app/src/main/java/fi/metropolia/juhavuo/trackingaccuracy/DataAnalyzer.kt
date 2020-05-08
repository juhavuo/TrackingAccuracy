package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.location.Location
import android.util.Log
import org.osmdroid.util.GeoPoint
import kotlin.math.hypot

class DataAnalyzer(val id: Int, val context: Context){

    private var measuredLocations: ArrayList<MeasuredLocation> = ArrayList()

    fun getMeasuredLocationsFromDatabase() {
        Thread {
            val db = RouteDB.get(context)
            measuredLocations = db.measuredLocationDao()
                .getLocationsOfRouteWithId(id) as ArrayList<MeasuredLocation>
            Log.i("test", "database operation complete, ${measuredLocations.size}")
        }.start()
    }


    fun getOriginalLocations(): ArrayList<MeasuredLocation>{
        return measuredLocations
    }

    fun getMeasuredLocationsAsGeoPoints(): ArrayList<GeoPoint>{
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for(measuredLocation in measuredLocations){
            geoPoints.add(GeoPoint(measuredLocation.latitude,measuredLocation.longitude))
        }
        return geoPoints
    }

    private fun getMeasuredLocationsAsGeoPoints(mlocations: ArrayList<MeasuredLocation>): ArrayList<GeoPoint>{
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for(measuredLocation in mlocations){
            geoPoints.add(GeoPoint(measuredLocation.latitude,measuredLocation.longitude))
        }
        return geoPoints
    }

    fun getAccuracies(): ArrayList<Float>{
        val accuracies: ArrayList<Float> = ArrayList()
        for(location in measuredLocations){
            if(location.accuracy == null){
                accuracies.add(0.0f)
            }else{
                accuracies.add(location.accuracy)
            }
        }

        return accuracies
    }

    //ttps://rosettacode.org/wiki/Ramer-Douglas-Peucker_line_simplification#Kotlin
    private fun rdpDistance(location: MeasuredLocation, startLocation : MeasuredLocation, endLocation: MeasuredLocation): Double{
        var dx = endLocation.longitude - startLocation.longitude
        var dy = endLocation.latitude - startLocation.latitude

        val pointList = measuredLocations

        val mag = hypot(dx,dy)
        if(mag>0.0){
            dx /= mag
            dy /= mag
        }

        val pvx = location.longitude - startLocation.longitude
        val pvy = location.latitude - startLocation.latitude

        val pvdot = dx*pvx + dy*pvy

        val ax = pvx - pvdot * dx
        val ay = pvy - pvdot * dy

        return hypot(ax, ay)
    }

    //ttps://rosettacode.org/wiki/Ramer-Douglas-Peucker_line_simplification#Kotlin
    private fun ramerDouglasPeucker(mlocations: ArrayList<MeasuredLocation> ,epsilon: Double): ArrayList<MeasuredLocation>{

        val calculatedLocations: ArrayList<MeasuredLocation> = ArrayList()

        if(mlocations.size<2){
            return calculatedLocations
        }

        val dmax = 0.0
        var index = 0
        val end = mlocations.size -1

        for (i in 1 until end){
            val d = rdpDistance(mlocations[i],mlocations[0],mlocations[end])
        }

        if(dmax> epsilon){
            var recResults1: ArrayList<MeasuredLocation> = ArrayList()
            var recResults2: ArrayList<MeasuredLocation> = ArrayList()
            val firstLine = mlocations.take(index+1) as ArrayList<MeasuredLocation>
            val lastLine= mlocations.drop(index) as ArrayList<MeasuredLocation>
            recResults1 = ramerDouglasPeucker(firstLine, epsilon)
            recResults2 = ramerDouglasPeucker(lastLine,epsilon)
            calculatedLocations.addAll(recResults1.take(recResults1.size-1))
            calculatedLocations.addAll(recResults2)
            if(calculatedLocations.size <2){
                calculatedLocations.clear()
                return calculatedLocations
            }
        }else{
            calculatedLocations.clear()
            calculatedLocations.add(mlocations.first())
            calculatedLocations.add(mlocations.last())
        }

        return calculatedLocations
    }

    /**
     * for algorithm 1: Ramer-Douglas-Pecker
     */
    fun getRDPGeoPoints(epsilon: Double): ArrayList<GeoPoint>{
        val locations = ramerDouglasPeucker(measuredLocations,epsilon)
        return getMeasuredLocationsAsGeoPoints(locations)
    }

}