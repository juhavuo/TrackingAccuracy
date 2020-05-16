package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context

class MapPreferencesHandler(context: Context){

    private val sharedPreferences = context.getSharedPreferences("map_preferences",Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private val accuraciesPrference = "accuracy"
    private val showLinesPreference = "showlines"
    private val algorithmPreferences = arrayOf("measured","algorithm1","algorithm2","algorithm3")
    private val epsilonPreference = "epsilon" //this would be better in database
    private val mapZoomPreference = "mapzoom"
    private val accuracyThresholdPreference = "accuracythreshold" //this would be better in database

    fun storeAccuracyPreference(showAccuracies: Boolean){
        editor.putBoolean(accuraciesPrference,showAccuracies)
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


    fun storeAccuracyTresholdPreference(accuracyThreshold: Int){
        editor.putInt(accuracyThresholdPreference, accuracyThreshold)
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

    fun getAccuracyThresholdPreference(): Int = sharedPreferences.getInt(accuracyThresholdPreference,1000)

    private fun checkArrayIndex(index: Int): Boolean = (index >= 0 || index < algorithmPreferences.size)


}