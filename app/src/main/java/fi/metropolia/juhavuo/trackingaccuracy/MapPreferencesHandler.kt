package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context

class MapPreferencesHandler(context: Context){

    private val sharedPreferences = context.getSharedPreferences("map_preferences",Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val accuraciesPrference = "accuracy"
    private val showLinesPreference = "showlines"
    private val showMeasuredPreference = "showmeasured"
    private val algorithmPreferences = arrayOf("measured","algorithm1","algorithm2","algorithm3")
    private val epsilonPreference = "epsilon"
    private val mapZoomPreference = "mapzoom"

    fun storeAccuracyPreference(showAccuracies: Boolean){
        editor.putBoolean(accuraciesPrference,showAccuracies)
        editor.apply()
    }

    fun storeShowLinesPreference(showLines: Boolean){
        editor.putBoolean(showLinesPreference,showLines)
        editor.apply()
    }

    fun storeAlgorithmPreference(index: Int, value: Boolean){
        if(checkArrayIndex(index)){
            editor.putBoolean(algorithmPreferences[index],value)
            editor.apply()
        }
    }

    fun storeEpsilonPreference(epsilon: Double){
        val epsilonString = epsilon.toString()
        editor.putString(epsilonPreference,epsilonString)
        editor.apply()
    }

    fun storeMapZoomPreference(zoomLevel: Double){
        val zoomLevelString = zoomLevel.toString()
        editor.putString(mapZoomPreference,zoomLevelString)
        editor.apply()
    }

    fun getAccuracyPreference(): Boolean = sharedPreferences.getBoolean(accuraciesPrference,true)

    fun getShowLinesPreference(): Boolean = sharedPreferences.getBoolean(showLinesPreference,true)

    fun getAmoutOfAlgorithmPreferences(): Int = algorithmPreferences.size

    fun getAlgorithmPreference(index: Int): Boolean{
        if(checkArrayIndex(index)){
            if(index == 0){
                return sharedPreferences.getBoolean(algorithmPreferences[index],true)
            }else {
                return sharedPreferences.getBoolean(algorithmPreferences[index], false)
            }
        }else{
            return false
        }
    }

    fun getEpsilonPreference(): Double{
        val epsilonString = sharedPreferences.getString(epsilonPreference,"0.00001")
        return epsilonString!!.toDouble()
    }

    fun getMapZoomPreference(): Double{
        val mapZoomString = sharedPreferences.getString(mapZoomPreference,"15.0")
        return mapZoomString!!.toDouble()
    }

    private fun checkArrayIndex(index: Int): Boolean = (index >= 0 || index < algorithmPreferences.size)


}