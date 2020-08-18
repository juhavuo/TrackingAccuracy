package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.numerical_fragment_recyclerview_row.view.*

/*
    For RecyclerView used in NumericalFragment. First version. Column width is determined
    from device width, witch is not suitable solution for all devices.
 */
class NumericalDataAdapter(var items: ArrayList<MeasuredLocation>, val context: Context):
        RecyclerView.Adapter<NumericalDataAdapter.NumerericalDataHolder>(){

    inner class NumerericalDataHolder(v: View): RecyclerView.ViewHolder(v){
        val lat_textview = v.numerical_fragment_rv_row_latitude
        val lng_textview = v.numerical_fragment_rv_row_longitude
        val acc_textview = v.numerical_fragment_rv_row_accuracy
        val bea_textview = v.numerical_fragment_rv_row_bearing
        val bea_acc_textview = v.numerical_fragment_rv_row_bearing_accuracy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumerericalDataHolder {
       val inflatedView = LayoutInflater.from(context).
               inflate(R.layout.numerical_fragment_recyclerview_row,parent,false)
        return NumerericalDataHolder(inflatedView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: NumerericalDataHolder, position: Int) {
        val fourDecimal = "%.4f"
        val twoDecimal = "%.2f"
        holder.lat_textview.text = fourDecimal.format(items[position].latitude)
        holder.lng_textview.text = fourDecimal.format(items[position].longitude)
        holder.acc_textview.text = twoDecimal.format(items[position].accuracy)
        holder.bea_textview.text = twoDecimal.format(items[position].bearing)
        holder.bea_acc_textview.text = twoDecimal.format(items[position].bearingAccuracy)
    }
}