package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_route_listing.*

class RouteListingActivity : AppCompatActivity() {

    private var routelist: ArrayList<Route> = ArrayList()
    private var routeListingAdapter: RouteListingAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_listing)

        val linearLayoutManager = LinearLayoutManager(this)
        routeListingAdapter = RouteListingAdapter(routelist,this)
        route_listing_recyclerview.layoutManager = linearLayoutManager
        route_listing_recyclerview.adapter = routeListingAdapter

        route_listing_recyclerview.addItemDecoration(RecyclerviewMarginDecoration(20))
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

        route_listing_close_button.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        route_listing_view_selected_button.setOnClickListener {
            if(routeListingAdapter!=null) {
                val ids = routeListingAdapter!!.getIdsOfSelectedRoutes()
                //Log.i("ids","$ids")
                if(ids.isNotEmpty()){
                    val intent = Intent(this,DataViewingActivity::class.java)
                    intent.putIntegerArrayListExtra("ids",ids)
                    startActivity(intent)
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
               routeListingAdapter?.updateItemsList(routelist)
            }
        }.start()
    }


}
