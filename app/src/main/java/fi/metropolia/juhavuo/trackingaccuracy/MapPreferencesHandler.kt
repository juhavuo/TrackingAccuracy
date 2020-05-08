package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context

class MapPreferencesHandler(context: Context){

    private val sharedPreferences = context.getSharedPreferences("map_preferences",Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val accuraciesPrference = "accuracy"
    private val showLinesPreference = "showlines"
    private val algorithmPreferences = arrayOf("algorithm1,algorithm2,algorithm3")

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

    fun getAccuracyPreference(): Boolean = sharedPreferences.getBoolean(accuraciesPrference,true)

    fun getShowLinesPreference(): Boolean = sharedPreferences.getBoolean(showLinesPreference,true)

    fun getAmoutOfAlgorithmPreferences(): Int = algorithmPreferences.size

    fun getAlgorithmPreference(index: Int): Boolean{
        if(checkArrayIndex(index)){
            return sharedPreferences.getBoolean(algorithmPreferences[index],false)
        }else{
            return false
        }
    }

    private fun checkArrayIndex(index: Int): Boolean = (index >= 0 || index < algorithmPreferences.size)


}