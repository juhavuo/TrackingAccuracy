package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context

class MapPreferencesHandler(context: Context){

    private val sharedPreferences = context.getSharedPreferences("map_preferences",Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()
    private val accuraciesPreference = "accuracy"
    private val bearingsPreference = "bearing"
    private val algorithmPreferences = arrayOf("measured","algorithm1","algorithm2","algorithm3","algorithm4")
    private val epsilonPreference = "epsilon" //this would be better in database
    private val mapZoomPreference = "mapzoom"
    private val accuracyThresholdPreference = "accuracythreshold" //this would be better in database
    private val runningMeanPreference = "runningmean"

    fun storeAccuracyPreference(showAccuracies: Boolean){
        editor.putBoolean(accuraciesPreference,showAccuracies)
        editor.apply()
    }

    fun storeBearingsPreference(showBearings: Boolean){
        editor.putBoolean(bearingsPreference,showBearings)
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


    fun storeAccuracyThresholdPreference(accuracyThreshold: Int){
        editor.putInt(accuracyThresholdPreference, accuracyThreshold)
        editor.apply()
    }

    fun storeRunningMeanPreference(amount: Int){
        editor.putInt(runningMeanPreference,amount)
        editor.apply()
    }

    fun getAccuracyPreference(): Boolean = sharedPreferences.getBoolean(accuraciesPreference,true)

    fun getBearingsPreference(): Boolean = sharedPreferences.getBoolean(bearingsPreference,false)

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

    fun getRunningMeanPreference(): Int = sharedPreferences.getInt(runningMeanPreference,3)

    private fun checkArrayIndex(index: Int): Boolean = (index >= 0 || index < algorithmPreferences.size)


}