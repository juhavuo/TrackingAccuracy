package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.route_listing_recycleview_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class RouteListingAdapter(var items: ArrayList<Route>, val context: Context):
    RecyclerView.Adapter<RouteListingAdapter.RoutesHolder>(){

    inner class RoutesHolder(v: View): RecyclerView.ViewHolder(v){
        val title_tv = v.route_listing_rv_row_title_tv
        val description_tv = v.route_listing_rv_row_description_tv
        val startingTime_tv = v.route_listing_rv_row_time_tv
        val view_button = v.route_listing_rv_row_view_button
        val delete_button = v.route_listing_rv_row_delete_button

        init {
            view_button.setOnClickListener {
                val intent = Intent(context,DataViewingActivity::class.java)
                intent.putExtra("routeid",items[adapterPosition].routeid)
                Log.i("test","${items[adapterPosition].routeid}")
                intent.putExtra("routename",items[adapterPosition].name)
                context.startActivity(intent)
            }
            delete_button.setOnClickListener {
                val database = RouteDB.get(context)
                val routeId = items[adapterPosition].routeid
                Thread{
                    database.routeDao().deleteRouteWithId(routeId)
                }.start()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoutesHolder {
        val inflatedView = LayoutInflater.from(context).
        inflate(R.layout.route_listing_recycleview_row,parent,false)
        return RoutesHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RoutesHolder, position: Int) {
        holder.title_tv.text = items[position].name
        holder.description_tv.text = items[position].description
        //https://stackoverflow.com/questions/47250263/kotlin-convert-timestamp-to-datetime Anga Koko
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = items[position].startingTime
        holder.startingTime_tv.text = DateFormat.format("hh:mm dd.MM.yyyy", calendar).toString()
    }

    fun updateItemsList(routes: ArrayList<Route>){
        items = routes
        notifyDataSetChanged()
    }


    //https://grokonez.com/kotlin/kotlin-array-sort-sortby-sortwith
    fun organizeByStartingTime(newestFirst: Boolean){
        items.sortWith(Comparator<Route> { p0, p1 ->
            when {
                p0.startingTime > p1.startingTime -> 1
                p0.startingTime == p1.startingTime -> 0
                else -> -1
            }
        })
        if(newestFirst)
            items.reverse()
        notifyDataSetChanged()
    }

    fun organizeByName(alphabetic: Boolean){
        items.sortWith(kotlin.Comparator<Route>{ p0,p1->
            when{
                p0.name.toLowerCase(Locale.ROOT) > p1.name.toLowerCase(Locale.ROOT) -> 1
                p0.name.toLowerCase(Locale.ROOT) == p1.name.toLowerCase(Locale.ROOT) -> 0
                else -> -1
            }
        })
        if(!alphabetic)
            items.reverse()
        notifyDataSetChanged()
    }
}