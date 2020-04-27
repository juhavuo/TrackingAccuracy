package fi.metropolia.juhavuo.trackingaccuracy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_data_viewing.*

class DataViewingActivity : AppCompatActivity() {

    private var menuShowing = false //for map fragment's menu
    private val dataAnalyzer = DataAnalyzer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_viewing)

        val routeid = intent.getIntExtra("routeid",-1)
        val routeName = intent.getStringExtra("routename")

        getLocationsOfRouteWithId(routeid)


    }

    private fun getLocationsOfRouteWithId(id: Int){
        Log.i("test","data viewing")
        Thread {
            val database = RouteDB.get(this)
            val measuredLocations =  database.measuredLocationDao()
                .getLocationsOfRouteWithId(id) as ArrayList<MeasuredLocation>
            dataAnalyzer.getMeasuredLocations(measuredLocations)
            Log.i("test","From dataAnalyzer ${measuredLocations.size}")
        }.start()
    }

}
