package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.util.Log

class DataAnalyzer(val id: Int, val context: Context){

    private var measuredLocations: ArrayList<MeasuredLocation> = ArrayList()

    init {
        Thread{
            val db = RouteDB.get(context)
            measuredLocations = db.measuredLocationDao().getLocationsOfRouteWithId(id) as ArrayList<MeasuredLocation>
        }.start()
    }

    fun getOriginalLocations(): ArrayList<MeasuredLocation>{
        return measuredLocations
    }
}