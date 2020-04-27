package fi.metropolia.juhavuo.trackingaccuracy

import android.util.Log

class DataAnalyzer(){

    private var measuredLocations: ArrayList<MeasuredLocation> = ArrayList()

    fun getMeasuredLocations(ml: ArrayList<MeasuredLocation>){
        measuredLocations = ml
    }
}