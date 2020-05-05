package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context

class MapPreferencesHandler(context: Context){

    private val sharedPreferences = context.getSharedPreferences("map_preferences",Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val accuraciesPrference = "accuracy"
    private val showLinesPreference = "showlines"

    fun storeAccuracyPreference(showAccuracies: Boolean){
        editor.putBoolean(accuraciesPrference,showAccuracies)
        editor.apply()
    }

    fun storeShowLinesPreference(showLines: Boolean){
        editor.putBoolean(showLinesPreference,showLines)
        editor.apply()
    }

    fun getAccuracyPreference(): Boolean = sharedPreferences.getBoolean(accuraciesPrference,true)

    fun getShowLinesPreference(): Boolean = sharedPreferences.getBoolean(showLinesPreference,true)

}