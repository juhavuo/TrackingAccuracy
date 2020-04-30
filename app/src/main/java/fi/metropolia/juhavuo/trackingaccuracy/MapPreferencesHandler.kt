package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context

class MapPreferencesHandler(context: Context){

    private val sharedPreferences = context.getSharedPreferences("map_preferences",Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    private val accuraciesPrference = "accuracy"

    fun storeAccuracyPreference(showAccuracies: Boolean){
        editor.putBoolean(accuraciesPrference,showAccuracies)
        editor.apply()
    }

    fun getAccuracyPreference(): Boolean = sharedPreferences.getBoolean(accuraciesPrference,true)

}