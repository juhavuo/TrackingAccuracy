package fi.metropolia.juhavuo.trackingaccuracy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_data_viewing.*

class DataViewingActivity : AppCompatActivity(), ShowMenuFragmentDelegate, CloseMenuFragmentDelegate {

    private var menuShowing = false //for map fragment's menu
    private lateinit var dataAnalyzer: DataAnalyzer
    private lateinit var mapFragment: MapFragment
    private lateinit var menuFragment: MenuFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_viewing)

        val routeid = intent.getIntExtra("routeid",-1)
        val routeName = intent.getStringExtra("routename")

        dataAnalyzer = DataAnalyzer(routeid,this.applicationContext)

        mapFragment = MapFragment()
        menuFragment = MenuFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container,mapFragment)
            .commit()

        mapFragment.getDataAnalyzer(dataAnalyzer)

        //getLocationsOfRouteWithId(routeid)


    }

    override fun showMenuFragment(fragment: MapFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,menuFragment)
            .commit()
    }

    override fun closeMenuFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,mapFragment)
            .commit()
    }


}
