package fi.metropolia.juhavuo.trackingaccuracy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.androidplot.xy.*

class GraphFragment: Fragment(){

    private var dataAnalyzer: DataAnalyzer? = null
    private lateinit var plot: XYPlot
    private lateinit var spinner: Spinner

    fun getDataAnalyzer(da: DataAnalyzer){
        dataAnalyzer = da
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graph,container,false)
        var distances: ArrayList<Float> = ArrayList()
        var timeIntervals: ArrayList<Double> = ArrayList()
        var altitudes: ArrayList<Double> = ArrayList()
        var speeds: ArrayList<Float> = ArrayList()
        val descriptions:Array<String> = resources.getStringArray(R.array.graph_option_listing_spinner_array)

        plot = view.findViewById(R.id.graph_fragment_plot)

        if(dataAnalyzer != null){
            distances = dataAnalyzer!!.getDistances()
            timeIntervals = dataAnalyzer!!.getTimeIntervals()
            altitudes = dataAnalyzer!!.getAltitudes()
            speeds = dataAnalyzer!!.getSpeeds()
        }

        spinner = view.findViewById(R.id.graph_fragment_spinner)
        val adapterForSpinner = context?.let {
            ArrayAdapter.createFromResource(
                it,R.array.graph_option_listing_spinner_array,android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapterForSpinner
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                when(pos){
                    0-> displayPlot(plot,changeDoubleListToNumbers(timeIntervals),changeFloatListToNumbers(distances),descriptions[0])
                    1-> displayPlot(plot,changeDoubleListToNumbers(timeIntervals),changeFloatListToNumbers(speeds),descriptions[1])
                    2-> displayPlot(plot,changeFloatListToNumbers(distances),changeDoubleListToNumbers(altitudes),descriptions[2])
                }
            }

        }


        return view
    }

    private fun displayPlot(plot: XYPlot,xValues: ArrayList<Number>, yValues: ArrayList<Number>, description: String){
        plot.clear()

        val series = SimpleXYSeries(xValues,yValues,description)
        val lineAndPointFormatter = LineAndPointFormatter(context,R.xml.line_point_formatter)
        plot.addSeries(series,lineAndPointFormatter)

    }

    private fun changeFloatListToNumbers(floatList: ArrayList<Float>): ArrayList<Number>{
        val numberList: ArrayList<Number> = ArrayList()
        for(f in floatList){
            numberList.add(f)
        }
        return numberList
    }

    private fun changeDoubleListToNumbers(doubleList: ArrayList<Double>): ArrayList<Number>{
        val numberList: ArrayList<Number> = ArrayList()
        for(d in doubleList){
            numberList.add(d)
        }
        return numberList
    }
}