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

        val ids = intent.getIntegerArrayListExtra("ids")
        val routeName = "TEMPLATE"
        Log.i("ids","ids from intent: $ids")

        dataAnalyzer = DataAnalyzer(ids,this.applicationContext)
        dataAnalyzer.getMeasuredLocationsFromDatabase()

        mapFragment = MapFragment()
        mapFragment.setRouteData(ids[0],routeName)

        menuFragment = MenuFragment()
        val graphFragment = GraphFragment()
        val numericalFragment = NumericalFragment()

        //MapFragment opens automatically, when DataViewingActivity starts
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container,mapFragment)
            .commit()

        mapFragment.setDataAnalyzer(dataAnalyzer)
        menuFragment.getDataAnalyzer(dataAnalyzer)
        numericalFragment.getDataAnalyzer(dataAnalyzer)
        graphFragment.getDataAnalyzer(dataAnalyzer)

        //changing fragment in the view
        data_viewing_bottom_bar.setOnNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.data_viewing_bm_graphs_item ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,graphFragment)
                        .commit()
                    true
                }
                R.id.data_viewing_bm_numerical ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,numericalFragment)
                        .commit()
                    true
                }
                R.id.data_viewing_bm_map ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container,mapFragment)
                        .commit()
                    true
                }else ->
                false
            }


        }


    }

    //MenuFragment opens in different place in MapFragment
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
