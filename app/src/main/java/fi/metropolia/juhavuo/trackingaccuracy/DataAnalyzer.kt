package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.location.Location
import android.util.Log
import org.osmdroid.util.GeoPoint
import kotlin.math.hypot

class DataAnalyzer(val id: Int, val context: Context) {

    private var measuredLocations: ArrayList<MeasuredLocation> = ArrayList()

    fun getMeasuredLocationsFromDatabase() {
        Thread {
            val db = RouteDB.get(context)
            measuredLocations = db.measuredLocationDao()
                .getLocationsOfRouteWithId(id) as ArrayList<MeasuredLocation>
            Log.i("test", "database operation complete, ${measuredLocations.size}")
        }.start()
    }

    fun getAmountOfLocations(): Int = measuredLocations.size


    fun getOriginalLocations(): ArrayList<MeasuredLocation> {
        return measuredLocations
    }

    fun getMeasuredLocationsAsGeoPoints(): ArrayList<GeoPoint> {
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for (measuredLocation in measuredLocations) {
            geoPoints.add(GeoPoint(measuredLocation.latitude, measuredLocation.longitude))
        }
        return geoPoints
    }

    private fun getMeasuredLocationsAsGeoPoints(mlocations: ArrayList<MeasuredLocation>): ArrayList<GeoPoint> {
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for (measuredLocation in mlocations) {
            geoPoints.add(GeoPoint(measuredLocation.latitude, measuredLocation.longitude))
        }
        return geoPoints
    }

    fun getAccuracies(): ArrayList<Float> {
        val accuracies: ArrayList<Float> = ArrayList()
        for (location in measuredLocations) {
            if (location.accuracy == null) {
                accuracies.add(0.0f)
            } else {
                accuracies.add(location.accuracy)
            }
        }

        return accuracies
    }

    //ttps://rosettacode.org/wiki/Ramer-Douglas-Peucker_line_simplification#Kotlin
    private fun perpendicularDistance(
        location: MeasuredLocation,
        startLocation: MeasuredLocation,
        endLocation: MeasuredLocation
    ): Double {
        var dx = endLocation.longitude - startLocation.longitude
        var dy = endLocation.latitude - startLocation.latitude

        val pointList = measuredLocations

        val mag = hypot(dx, dy)
        if (mag > 0.0) {
            dx /= mag
            dy /= mag
        }

        val pvx = location.longitude - startLocation.longitude
        val pvy = location.latitude - startLocation.latitude

        val pvdot = dx * pvx + dy * pvy

        val ax = pvx - pvdot * dx
        val ay = pvy - pvdot * dy

        return hypot(ax, ay)
    }


    fun ramerDouglasPeucker(
        pointList: List<MeasuredLocation>,
        epsilon: Double,
        out: MutableList<MeasuredLocation>
    ) {
        if (pointList.size < 2) throw IllegalArgumentException("Not enough points to simplify")

        // Find the point with the maximum distance from line between start and end
        var dmax = 0.0
        var index = 0
        val end = pointList.size - 1
        for (i in 1 until end) {
            val d = perpendicularDistance(pointList[i], pointList[0], pointList[end])
            if (d > dmax) {
                index = i; dmax = d
            }
        }

        // If max distance is greater than epsilon, recursively simplify
        if (dmax > epsilon) {
            val recResults1 = mutableListOf<MeasuredLocation>()
            val recResults2 = mutableListOf<MeasuredLocation>()
            val firstLine = pointList.take(index + 1)
            val lastLine = pointList.drop(index)
            ramerDouglasPeucker(firstLine, epsilon, recResults1)
            ramerDouglasPeucker(lastLine, epsilon, recResults2)

            // build the result list
            out.addAll(recResults1.take(recResults1.size - 1))
            out.addAll(recResults2)
            if (out.size < 2) throw RuntimeException("Problem assembling output")
        } else {
            // Just return start and end points
            out.clear()
            out.add(pointList.first())
            out.add(pointList.last())
        }
    }

    /**
     * for algorithm 1: Ramer-Douglas-Pecker
     */
    fun getAlgorithm1GeoPoints(epsilon: Double): ArrayList<GeoPoint> {
        val locations: ArrayList<MeasuredLocation> = ArrayList()
        ramerDouglasPeucker(measuredLocations, epsilon, locations)
        return getMeasuredLocationsAsGeoPoints(locations)
    }

    fun getKalmanFilteredGeoPoints(): ArrayList<GeoPoint> {
        val kalmanGeoPoints: ArrayList<GeoPoint> = ArrayList()
        val kalmanFilter = KalmanFilter(3f)



        kalmanFilter.setState(
            measuredLocations[0].latitude,
            measuredLocations[0].longitude,
            measuredLocations[0].accuracy,
            measuredLocations[0].timestamp
        )
        for(index in 1 until measuredLocations.size){
            kalmanFilter.process(measuredLocations[index].latitude
            ,measuredLocations[index].longitude
            ,measuredLocations[index].accuracy
            ,measuredLocations[index].timestamp)

            kalmanGeoPoints.add(GeoPoint(kalmanFilter.lat,kalmanFilter.lng))
        }

        return kalmanGeoPoints

    }

    /*
        Get smallest and biggest accuracy value of measured locations of route
        The extremes are returned as array, in which: index 0: smallest, index 1: biggest
     */
    fun getAccuracyExtremes(): FloatArray {
        if(measuredLocations.size==0){
            return floatArrayOf(0f,0f)
        }else if(measuredLocations.size ==1) {
            return floatArrayOf(measuredLocations[0].accuracy,measuredLocations[0].accuracy)
        }else {
            val accuracies = getAccuracies()
            accuracies.sort()
            return floatArrayOf(accuracies[0],accuracies.last())
        }
    }

    fun getAmountOfPointsToBeRemoved(threshold_accuracy: Float): Int{
        val accuracies = getAccuracies()
        var count = 0
        for(accuracy in accuracies){
            if(accuracy> threshold_accuracy){
                ++count
            }
        }

        return count
    }

    fun getDistances(): ArrayList<Float>{

        val distances: ArrayList<Float> = ArrayList()
        if(measuredLocations.size>0){
            distances.add(0f)
        }
        if(measuredLocations.size>1){
            var results: FloatArray = FloatArray(1)
            for(index in 1 until measuredLocations.size){
                Location.distanceBetween(measuredLocations[index-1].latitude,measuredLocations[index-1].longitude,
                measuredLocations[index].latitude,measuredLocations[index].longitude,results)
                distances.add(results[0])
            }
        }

        return distances
    }

    fun getTimeIntervals(): ArrayList<Double>{
        val timeIntervals: ArrayList<Double> = ArrayList()
        if(measuredLocations.size>0){
            timeIntervals.add(0.0)
        }
        if(measuredLocations.size>1){
            for(index in 1 until measuredLocations.size){
                timeIntervals.add((measuredLocations[index].timestamp-measuredLocations[index-1].timestamp)/1000.0)
            }
        }

        return timeIntervals
    }

    fun getAltitudes(): ArrayList<Double>{
        val altitudes: ArrayList<Double> = ArrayList()
        for(ml in measuredLocations){
            altitudes.add(ml.altitude)
        }
        return altitudes
    }

    fun getSpeeds(): ArrayList<Float>{
        val speeds: ArrayList<Float> = ArrayList()
        for(ml in measuredLocations){
            speeds.add(ml.speed)
        }
        return speeds
    }

    fun calculateAccuracyFromBarReading(s: Int, sMax: Int): Float {
        val extremes = getAccuracyExtremes()
        return (extremes[1] - extremes[0]) * s / sMax + extremes[0]
    }

    private fun calculateBarReadingFromAccuracy(a: Float, sMax: Int): Int{
        val extremes = getAccuracyExtremes()
        return (sMax*(a-extremes[0])/(extremes[1]-extremes[0])).toInt()
    }

    fun getRemainingLocations(threshold_accuracy: Float): ArrayList<GeoPoint>{
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for(mlocation in measuredLocations){
            if(mlocation.accuracy <= threshold_accuracy){
                geoPoints.add(GeoPoint(mlocation.latitude,mlocation.longitude))
            }
        }

        return geoPoints
    }

    private fun getLocationsOrganizedByAccuracy(): ArrayList<MeasuredLocation>{
        val cloneOfMeasuredLocations = measuredLocations.clone() as ArrayList<MeasuredLocation>
        cloneOfMeasuredLocations.sortWith(Comparator { p0, p1 ->
            when {
                p0.accuracy > p1.accuracy -> 1
                p0.accuracy == p1.accuracy -> 0
                else -> -1
            }
        })

        return cloneOfMeasuredLocations
    }


}