package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import java.io.FileWriter
import java.lang.Exception

class JsonSender(mContext: Context){

    private val context = mContext
    private var jsonString = ""
    private var fileName = ""
    private var filePath = ""

    fun createJsonFromDatabase(){
        val routeJsons: ArrayList<RouteJson> = ArrayList()
        val db = RouteDB.get(context)
        val routes = db.routeDao().getRoutes()
        for(route in routes){
            val locations = db.measuredLocationDao().getLocationsOfRouteWithId(route.routeid)
            routeJsons.add(RouteJson(route, locations as ArrayList<MeasuredLocation>))
        }
        val gson = Gson()
        jsonString = gson.toJson(routeJsons)
        Log.i("test",jsonString)

        val timeStamp = System.currentTimeMillis()
        fileName = "database${timeStamp}.json"

    }

    fun convertToFile(){
        if(jsonString.isNotEmpty() && fileName.isNotEmpty()){
            filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()+"/$fileName"
            fileName = ""
            Log.i("test",filePath)
            var file = FileWriter(filePath)
            try{
                file.write(jsonString)
                file.flush()
                file.close()
                Log.i("test","success")
            }catch (e: Exception){
                Log.e("test",e.toString())
            }

        }
    }

    fun sendDataAsIntent(){
      if(filePath.isNotEmpty()){

          filePath = ""
      }
    }
}