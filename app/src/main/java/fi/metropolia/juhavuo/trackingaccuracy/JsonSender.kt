package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.google.gson.Gson
import java.io.*
import java.lang.Exception

class JsonSender(mContext: Context){

    private val context = mContext
    private var jsonString = ""
    private var fileName = ""
    private var filePath = ""
    private var fileURI: Uri? = null

    /*
        Fetches all data from database and forms Json structure stored in json string
        also gives a filename to be used when saving the file
     */
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
        val timeStamp = System.currentTimeMillis()
        fileName = "database${timeStamp}.json"

    }

    /*
        Takes as parameter a MapFragmentJson, converts it in json string
        also gives a filename to be used when saving the file
     */
    fun createJsonFromCalculatedValues(mapFragmentJson: MapFragmentJson){
        val gson = Gson()
        jsonString = gson.toJson(mapFragmentJson)
        val timestamp = System.currentTimeMillis()
        fileName = "mappedRoutes${timestamp}.json"
    }

    /*
        Creates a file from the json object and saves it to external files directory: documents
        uses fileName-parameter
     */
    fun convertToFile(){
        if(jsonString.isNotEmpty() && fileName.isNotEmpty()){
            filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()+"/$fileName"
            Log.i("test",filePath)
            val file = File(filePath)
            try{
                val out = BufferedWriter(FileWriter(file))
                out.write(jsonString)
                out.close()
                fileURI = FileProvider.getUriForFile(context,
                    "fi.metropolia.juhavuo.trackingaccuracy", file)
                Log.i("test","success")
            }catch (e: IOException){
                Log.e("test",e.toString())
            }
        }
    }

    /*
        Sends file as sharing intent and starts external activity
        that asks how to share the file (for example to Google Drive)
     */
    fun sendDataAsIntent(){
      if(filePath.isNotEmpty() && fileURI != null){
          val sharingIntent = Intent(Intent.ACTION_SEND)
          sharingIntent.type = "text/json"
          sharingIntent.putExtra(Intent.EXTRA_STREAM,fileURI)
          context.startActivity(sharingIntent)
          filePath = ""

      }
    }
}