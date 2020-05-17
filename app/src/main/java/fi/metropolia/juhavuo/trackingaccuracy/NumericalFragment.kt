package fi.metropolia.juhavuo.trackingaccuracy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NumericalFragment: Fragment(){

    private var dataAnalyzer: DataAnalyzer? = null
    private lateinit var measuredLocations: ArrayList<MeasuredLocation>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_numerical,container,false)


        if(dataAnalyzer!=null){
            measuredLocations = dataAnalyzer!!.getOriginalLocations()
        }else{
            measuredLocations = ArrayList()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.numerical_fragment_recyclerview)
        val linearLayoutManager = LinearLayoutManager(context)
        val numericalDataAdapter = context?.let { NumericalDataAdapter(measuredLocations, it) }
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = numericalDataAdapter

        return view
    }

    fun getDataAnalyzer(da: DataAnalyzer){
        dataAnalyzer = da
    }

}