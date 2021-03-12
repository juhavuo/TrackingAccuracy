package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.names_listing_recycleview_row.view.*

class MapFragmentNameListingAdapter(val context: Context): RecyclerView.Adapter<MapFragmentNameListingAdapter.RouteNamesHolder>() {

    inner class RouteNamesHolder(v: View): RecyclerView.ViewHolder(v){
        val image = v.names_listing_rv_row_image
        val textview = v.names_listing_rv_row_textView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteNamesHolder {
       val inflatedView = LayoutInflater.from(context).
               inflate(R.layout.names_listing_recycleview_row,parent,false)
        return RouteNamesHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: RouteNamesHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}