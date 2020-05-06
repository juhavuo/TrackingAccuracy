package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.util.Log
import org.osmdroid.util.GeoPoint

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

}