package fi.metropolia.juhavuo.trackingaccuracy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_route_listing.*

class RouteListingActivity : AppCompatActivity() {

    private lateinit var routelist: ArrayList<Route>
    private lateinit var routeListingAdapter: RouteListingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_listing)
        getRoutes()

    }

    private fun getRoutes(){
        Thread{
            val database = RouteDB.get(this)
            routelist = database.routeDao().getRoutes() as ArrayList<Route>
            this@RouteListingActivity.runOnUiThread {
                val linearLayoutManager = LinearLayoutManager(this)
                routeListingAdapter = RouteListingAdapter(routelist,this)
                route_listing_recyclerview.layoutManager = linearLayoutManager
                route_listing_recyclerview.adapter = routeListingAdapter
            }
        }.start()
    }


}
