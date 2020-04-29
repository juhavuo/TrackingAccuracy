package fi.metropolia.juhavuo.trackingaccuracy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_data_viewing.*

class DataViewingActivity : AppCompatActivity() {

    private var menuShowing = false //for map fragment's menu
    private lateinit var dataAnalyzer: DataAnalyzer
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_viewing)

        val routeid = intent.getIntExtra("routeid",-1)
        val routeName = intent.getStringExtra("routename")

        dataAnalyzer = DataAnalyzer(routeid,this.applicationContext)

        mapFragment = MapFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container,mapFragment)
            .commit()

        mapFragment.getDataAnalyzer(dataAnalyzer)

        //getLocationsOfRouteWithId(routeid)


    }




}
