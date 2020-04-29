package fi.metropolia.juhavuo.trackingaccuracy

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

class MapFragment: Fragment(){

    private lateinit var map: MapView
    private var dataAnalyzer: DataAnalyzer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = context
        Configuration.getInstance().load(ctx,
            PreferenceManager.getDefaultSharedPreferences(ctx))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map,container,false)
        map = view.findViewById<MapView>(R.id.map_fragment_map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        return view
    }

    fun getDataAnalyzer(da: DataAnalyzer){
        dataAnalyzer = da
    }

    override fun onStart() {
        super.onStart()
        if(dataAnalyzer!=null){
            val locations = dataAnalyzer!!.getOriginalLocations()
            Log.i("test","data analyzer not null, locations: ${locations.size}")
        }
    }





}