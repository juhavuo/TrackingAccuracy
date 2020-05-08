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
    private fun perpendicularDistance(location: MeasuredLocation, startLocation : MeasuredLocation, endLocation: MeasuredLocation): Double{
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


    fun ramerDouglasPeucker(pointList: List<MeasuredLocation>, epsilon: Double, out: MutableList<MeasuredLocation>) {
        if (pointList.size < 2) throw IllegalArgumentException("Not enough points to simplify")

        // Find the point with the maximum distance from line between start and end
        var dmax = 0.0
        var index = 0
        val end = pointList.size - 1
        for (i in 1 until end) {
            val d = perpendicularDistance(pointList[i], pointList[0], pointList[end])
            if (d > dmax) { index = i; dmax = d }
        }

        // If max distance is greater than epsilon, recursively simplify
        if (dmax > epsilon) {
            val recResults1 = mutableListOf<MeasuredLocation>()
            val recResults2 = mutableListOf<MeasuredLocation>()
            val firstLine = pointList.take(index + 1)
            val lastLine  = pointList.drop(index)
            ramerDouglasPeucker(firstLine, epsilon, recResults1)
            ramerDouglasPeucker(lastLine, epsilon, recResults2)

            // build the result list
            out.addAll(recResults1.take(recResults1.size - 1))
            out.addAll(recResults2)
            if (out.size < 2) throw RuntimeException("Problem assembling output")
        }
        else {
            // Just return start and end points
            out.clear()
            out.add(pointList.first())
            out.add(pointList.last())
        }
    }

    /**
     * for algorithm 1: Ramer-Douglas-Pecker
     */
    fun getAlgorithm1GeoPoints(epsilon: Double): ArrayList<GeoPoint>{
        val locations: ArrayList<MeasuredLocation> = ArrayList()
        ramerDouglasPeucker(measuredLocations,epsilon,locations)
        return getMeasuredLocationsAsGeoPoints(locations)
    }

}