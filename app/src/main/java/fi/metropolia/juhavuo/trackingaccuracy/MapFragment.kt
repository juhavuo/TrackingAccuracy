package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

class MapFragment: Fragment(){

    private lateinit var map: MapView
    private var dataAnalyzer: DataAnalyzer? = null
    private var delegate: ShowMenuFragmentDelegate? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ShowMenuFragmentDelegate){
            delegate = context
        }
    }

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
        val menubutton = view.findViewById<ImageButton>(R.id.map_fragment_menu_button)
        menubutton.setOnClickListener {
            delegate?.showMenuFragment(this)
        }
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

interface ShowMenuFragmentDelegate{
    fun showMenuFragment(fragment: MapFragment)
}