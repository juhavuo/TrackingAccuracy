package fi.metropolia.juhavuo.trackingaccuracy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_route_listing.*

class RouteListingActivity : AppCompatActivity() {

    private lateinit var routelist: ArrayList<Route>
    private var routeListingAdapter: RouteListingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_listing)

        getRoutes()

        val adapterForSpinner = ArrayAdapter.createFromResource(
            this,R.array.route_listing_spinner_array,android.R.layout.simple_spinner_dropdown_item)
        route_listing_spinner.adapter = adapterForSpinner

        route_listing_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
               when(pos){
                   0 -> routeListingAdapter?.organizeByStartingTime(true)
                   1 -> routeListingAdapter?.organizeByStartingTime(false)
                   2 -> routeListingAdapter?.organizeByName(true)
                   3 -> routeListingAdapter?.organizeByName(false)
                   else -> routeListingAdapter?.organizeByStartingTime(true)
               }
            }

        }

    }

    private fun getRoutes(){
        Thread{
            val database = RouteDB.get(this)
            routelist = database.routeDao().getRoutes() as ArrayList<Route>
            routelist.reverse()
            this@RouteListingActivity.runOnUiThread {
                val linearLayoutManager = LinearLayoutManager(this)
                routeListingAdapter = RouteListingAdapter(routelist,this)
                route_listing_recyclerview.layoutManager = linearLayoutManager
                route_listing_recyclerview.adapter = routeListingAdapter
            }
        }.start()
    }


}
