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
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapFragment: Fragment(){

    private var map: MapView? = null
    private var dataAnalyzer: DataAnalyzer? = null
    private var delegate: ShowMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler
    private var measuredPolyline = Polyline()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mapPreferencesHandler = MapPreferencesHandler(context)

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
        map?.setTileSource(TileSourceFactory.MAPNIK)
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
        val showAccuracies = mapPreferencesHandler.getAccuracyPreference()
        Log.i("test","map fragment on start, accuracies show: $showAccuracies")
        if(dataAnalyzer!=null){
            val geoPoints = dataAnalyzer!!.getMeasuredLocationsAsGeoPoints()
            if(geoPoints.isNotEmpty()){
                map?.controller?.setZoom(14.0)
                map?.controller?.setCenter(geoPoints[0])

                if(geoPoints.size<2){
                    drawLocations(true,geoPoints)
                }else{
                    if(mapPreferencesHandler.getShowLinesPreference()) {
                        drawLocations(true, geoPoints)
                    }else{
                        drawLocations(false,geoPoints)
                    }
                }
            }

            map?.invalidate()
        }

    }

    private fun drawLocations(drawAsLine:Boolean, gpoints: ArrayList<GeoPoint>){
        if(map==null){
            Log.i("test", "map null")
        }
        if(drawAsLine){
            measuredPolyline.setPoints(gpoints)
            map?.overlayManager?.add(measuredPolyline)
            map?.invalidate()
        }else{
            for(gp in gpoints){
                map?.overlayManager?.remove(measuredPolyline)
                val marker = Marker(map)
                marker.position = gp
                marker.icon = resources.getDrawable(R.drawable.map_marker,null)
                marker.setAnchor(0.5f,0.5f)
                map?.overlays?.add(marker)
                map?.invalidate()
            }
        }
    }


}

interface ShowMenuFragmentDelegate{
    fun showMenuFragment(fragment: MapFragment)
}