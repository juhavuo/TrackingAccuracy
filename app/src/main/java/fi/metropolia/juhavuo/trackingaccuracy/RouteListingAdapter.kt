package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.route_listing_recycleview_row.view.*

class RouteListingAdapter(val items: ArrayList<Route>, val context: Context):
    RecyclerView.Adapter<RouteListingAdapter.RoutesHolder>(){

    inner class RoutesHolder(v: View): RecyclerView.ViewHolder(v){
        val title_tv = v.route_listing_rv_row_title_tv
        val description_tv = v.route_listing_rv_row_description_tv
        val startingTime_tv = v.route_listing_rv_row_time_tv
        val view_button = v.route_listing_rv_row_view_button
        val delete_button = v.route_listing_rv_row_delete_button
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
        holder.startingTime_tv.text = items[position].startingTime.toString()
    }
}