package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.location.Location
import android.util.Log
import org.osmdroid.util.GeoPoint
import kotlin.math.hypot

/*
    This class is for using filtering algorithms and calculating the data values to be shown
    in MapFragment and GraphFragment.
 */
class DataAnalyzer(val ids: ArrayList<Int>, val context: Context) {

    private var measuredLocations: ArrayList<MeasuredLocation> = ArrayList()

    /*
        Fetches location data of the specific data set using id of that set (route).
     */
    fun getMeasuredLocationsFromDatabase() {
        Thread {
            val db = RouteDB.get(context)
            measuredLocations = db.measuredLocationDao()
                .getLocationsOfRouteWithId(ids[0]) as ArrayList<MeasuredLocation>
            Log.i("test", "database operation complete, ${measuredLocations.size}")
        }.start()
    }


    fun getAmountOfLocations(): Int = measuredLocations.size

    /*
        To get the measured unfiltered locations. Returns: ArrayList of MeasuredLocation-objects.
     */
    fun getOriginalLocations(): ArrayList<MeasuredLocation> {
        return measuredLocations
    }

    /*
        To get the measured unfiltered locations as ArrayList of GeoPoint-objects.
     */
    fun getMeasuredLocationsAsGeoPoints(): ArrayList<GeoPoint> {
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for (measuredLocation in measuredLocations) {
            geoPoints.add(GeoPoint(measuredLocation.latitude, measuredLocation.longitude))
        }
        return geoPoints
    }

    /*
        Get ArrayList of Geopoints from given ArrayList of MeasuredLocation-objects
     */
    private fun getMeasuredLocationsAsGeoPoints(mlocations: ArrayList<MeasuredLocation>): ArrayList<GeoPoint> {
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for (measuredLocation in mlocations) {
            geoPoints.add(GeoPoint(measuredLocation.latitude, measuredLocation.longitude))
        }
        return geoPoints
    }

    /*
        Get accuracies as ArrayList
     */
    fun getAccuracies(): ArrayList<Float> {
        val accuracies: ArrayList<Float> = ArrayList()
        for (location in measuredLocations) {
            accuracies.add(location.accuracy)
        }

        return accuracies
    }

    /*
        Get average accuracy
     */
    fun getAverageAccuracy(): Double{
        val accuracies = getAccuracies()
        return accuracies.average()
    }

    /*
        Get bearings as ArrayList
     */
    fun getBearings(): ArrayList<Float> {
        val bearings: ArrayList<Float> = ArrayList()
        for (location in measuredLocations) {
            bearings.add(location.bearing)
        }
        return bearings
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
    ): Boolean {
        if (pointList.size < 2) {
            return false
        }

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
            if (out.size < 2) {
                return false
            }
        } else {
            // Just return start and end points
            out.clear()
            out.add(pointList.first())
            out.add(pointList.last())
        }

        return true
    }

    /**
     * for algorithm 1: Ramer-Douglas-Pecker
     */
    fun getAlgorithm1GeoPoints(epsilon: Double): ArrayList<GeoPoint> {
        val locations: ArrayList<MeasuredLocation> = ArrayList() //for output of locations
        val success = ramerDouglasPeucker(measuredLocations, epsilon, locations)
        if (!success) {
            locations.clear()
        }
        return getMeasuredLocationsAsGeoPoints(locations)

    }

    fun getKalmanFilteredGeoPoints(): ArrayList<GeoPoint> {
        val kalmanGeoPoints: ArrayList<GeoPoint> = ArrayList()
        var speed = getSpeedMeanValue()
        speed *= 1.2f //this can be changed, for better values
        if (speed < 3f) {
            speed = 3f
        }

        val kalmanFilter = KalmanFilter(speed)

        kalmanFilter.setState(
            measuredLocations[0].latitude,
            measuredLocations[0].longitude,
            measuredLocations[0].accuracy,
            measuredLocations[0].timestamp
        )
        for (index in 1 until measuredLocations.size) {
            kalmanFilter.process(
                measuredLocations[index].latitude
                , measuredLocations[index].longitude
                , measuredLocations[index].accuracy
                , measuredLocations[index].timestamp
            )

            kalmanGeoPoints.add(GeoPoint(kalmanFilter.lat, kalmanFilter.lng))
        }

        return kalmanGeoPoints

    }

    private fun getSpeedMeanValue(): Float {
        var speed = 0f
        for (ml in measuredLocations) {
            speed += ml.speed
        }

        return speed / measuredLocations.size
    }

    /*
        Get smallest and biggest accuracy value of measured locations of route
        The extremes are returned as array, in which: index 0: smallest, index 1: biggest
     */
    fun getAccuracyExtremes(): FloatArray {
        if (measuredLocations.size == 0) {
            return floatArrayOf(0f, 0f)
        } else if (measuredLocations.size == 1) {
            return floatArrayOf(measuredLocations[0].accuracy, measuredLocations[0].accuracy)
        } else {
            val accuracies = getAccuracies()
            accuracies.sort()
            return floatArrayOf(accuracies[0], accuracies.last())
        }
    }

    fun getAmountOfPointsToBeRemoved(threshold_accuracy: Float): Int {
        val accuracies = getAccuracies()
        var count = 0
        for (accuracy in accuracies) {
            if (accuracy > threshold_accuracy) {
                ++count
            }
        }

        return count
    }

    fun getMovingAverages(amount: Int, withWeights: Boolean): ArrayList<GeoPoint> {
        var a = amount
        if (a < 2) {
            a = 2
        } else if (a > 10) {
            a = 10
        }
        val latitudes = measuredLocations.map { it.latitude }
        val longitudes = measuredLocations.map { it.longitude }

        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        if (withWeights) {
            val weights = measuredLocations.map {
                if (it.accuracy != 0f) {
                    1 / it.accuracy
                } else {
                    1f //rare occation, in this case no accuracy is obtained, so standard weight is used
                }
            }
            val latitudeAverages = movingAverageWithWeigths(latitudes,a,weights)
            val longitudeAverages = movingAverageWithWeigths(longitudes,a,weights)
            for(index in 0 until latitudeAverages.size){
                geoPoints.add(GeoPoint(latitudeAverages[index],longitudeAverages[index]))
            }
        } else {
            val latitudeAverages = movingAverage(latitudes, a)
            val longitudeAverages = movingAverage(longitudes, a)
            for (index in 0 until latitudeAverages.size) {
                geoPoints.add(GeoPoint(latitudeAverages[index], longitudeAverages[index]))
            }
        }
        return geoPoints
    }

    private fun movingAverage(list: List<Double>, amount: Int): ArrayList<Double> {
        val averages: ArrayList<Double> = ArrayList()
        for (index in amount..list.size) {
            averages.add(list.subList(index - amount, index).average())
        }
        return averages
    }

    private fun movingAverageWithWeigths(
        list: List<Double>,
        amount: Int,
        weights: List<Float>
    ): ArrayList<Double> {
        val averages: ArrayList<Double> = ArrayList()
        for (i in amount..list.size) {
            var weighedSum: Double = 0.0
            var sumOfWeights: Float = 0f
            for (j in i - amount until i) {
                weighedSum += weights[j] * list[j]
                sumOfWeights += weights[j]
            }
            averages.add(weighedSum / sumOfWeights)
        }

        return averages
    }


    /*
        Calculates distances between adjacent locations in
     */
    fun getDistances(): ArrayList<Float> {

        val distances: ArrayList<Float> = ArrayList()
        if (measuredLocations.size > 0) {
            distances.add(0f)
        }
        if (measuredLocations.size > 1) {
            var results: FloatArray = FloatArray(1)
            for (index in 1 until measuredLocations.size) {
                Location.distanceBetween(
                    measuredLocations[index - 1].latitude, measuredLocations[index - 1].longitude,
                    measuredLocations[index].latitude, measuredLocations[index].longitude, results
                )
                distances.add(results[0])
            }
        }

        return distances
    }

    /*
        Calculates distances between adjacent GeoPoints in GeoPoint-ArrayList
        and returns them as ArrayList
     */
    fun getDistances(gPoints: ArrayList<GeoPoint>): ArrayList<Float> {
        val distances: ArrayList<Float> = ArrayList()
        if (gPoints.size > 0) {
            distances.add(0f)
        }
        if (gPoints.size > 1) {
            var results: FloatArray = FloatArray(1)
            for (index in 1 until gPoints.size) {
                Location.distanceBetween(
                    gPoints[index - 1].latitude, gPoints[index - 1].longitude,
                    gPoints[index].latitude, gPoints[index].longitude, results
                )
                distances.add(results[0])
            }

        }
        return distances
    }

    fun getCumulativeDistances(): ArrayList<Float> {
        val distances = getDistances()
        val travelledDistances: ArrayList<Float> = ArrayList()
        var travelled = 0f
        for (d in distances) {
            travelled += d
            travelledDistances.add(travelled)
        }
        return travelledDistances
    }

    fun getLengthOfRoute(gPoints: ArrayList<GeoPoint>): Float {
        var lengthOfRoute = 0f
        var distances = getDistances(gPoints)
        for (d in distances) {
            lengthOfRoute += d
        }
        return lengthOfRoute
    }

    fun getTimes(): ArrayList<Double> {
        val timeIntervals: ArrayList<Double> = ArrayList()
        if (measuredLocations.size > 0) {
            timeIntervals.add(0.0)
        }
        if (measuredLocations.size > 1) {
            for (index in 1 until measuredLocations.size) {
                timeIntervals.add((measuredLocations[index].timestamp - measuredLocations[0].timestamp) / 1000.0)
            }
        }

        return timeIntervals
    }

    /*
        Get the altitudes as ArrayList
     */
    fun getAltitudes(): ArrayList<Double> {
        val altitudes: ArrayList<Double> = ArrayList()
        for (ml in measuredLocations) {
            altitudes.add(ml.altitude)
        }
        return altitudes
    }

    /*
        Get the speeds as ArrayList
     */
    fun getSpeeds(): ArrayList<Float> {
        val speeds: ArrayList<Float> = ArrayList()
        for (ml in measuredLocations) {
            speeds.add(ml.speed)
        }
        return speeds
    }

    /*
        To convert SeekBar-value to accuracy in MenuFragment
     */
    fun calculateAccuracyFromBarReading(s: Int, sMax: Int): Float {
        val extremes = getAccuracyExtremes()
        return (extremes[1] - extremes[0]) * s / sMax + extremes[0]
    }

    /*
        Removes all locations that has accuracy in meters above the threshold_accuracy
        (those that are too inaccurate). Returns: ArrayList of GeoPoint-objects
     */
    fun getRemainingLocations(threshold_accuracy: Float): ArrayList<GeoPoint> {
        val geoPoints: ArrayList<GeoPoint> = ArrayList()
        for (mlocation in measuredLocations) {
            if (mlocation.accuracy <= threshold_accuracy) {
                geoPoints.add(GeoPoint(mlocation.latitude, mlocation.longitude))
            }
        }

        return geoPoints
    }

}