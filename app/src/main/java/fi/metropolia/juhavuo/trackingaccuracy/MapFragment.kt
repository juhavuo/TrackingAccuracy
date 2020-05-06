package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MapFragment: Fragment(){

    private var map: MapView? = null
    private var dataAnalyzer: DataAnalyzer? = null
    private var delegate: ShowMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler
    private var measuredPolyline: Polyline? = null
    private val polygons: ArrayList<Polygon> = ArrayList()


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
        val title = view.findViewById<TextView>(R.id.map_fragment_title)
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

        measuredPolyline = Polyline()

        val showAccuracies = mapPreferencesHandler.getAccuracyPreference()
        Log.i("test","map fragment on start, accuracies show: $showAccuracies")
        if(dataAnalyzer!=null){
            val geoPoints = dataAnalyzer!!.getMeasuredLocationsAsGeoPoints()
            if(geoPoints.isNotEmpty()){
                map?.controller?.setZoom(14.0)
                map?.controller?.setCenter(geoPoints[0])

                if(geoPoints.size>1){
                    if(mapPreferencesHandler.getShowLinesPreference()) {
                        drawLocations(true, geoPoints)
                    }else{
                        drawLocations(false,geoPoints)
                    }
                }else{
                    drawLocations(true,geoPoints)
                }

                if(mapPreferencesHandler.getAccuracyPreference()){
                    constructPolygons(geoPoints,dataAnalyzer!!.getAccuracies())
                }
            }

            map?.invalidate()
        }

    }

    private fun drawLocations(drawAsLine:Boolean, gpoints: ArrayList<GeoPoint>){
        if(map==null){
            Log.i("test", "map null")
        }
        if(drawAsLine && gpoints != null){
            measuredPolyline?.setPoints(gpoints)
            map?.overlayManager?.add(measuredPolyline)
            map?.invalidate()
        }else{
            for(gp in gpoints){
                //map?.overlayManager?.remove(measuredPolyline)
                val marker = Marker(map)
                marker.position = gp
                marker.icon = resources.getDrawable(R.drawable.map_marker,null)
                marker.setAnchor(0.5f,0.5f)
                map?.overlays?.add(marker)
                map?.invalidate()
            }
        }
    }

    private fun constructPolygons(gpoints: ArrayList<GeoPoint>, accuracies: ArrayList<Float>){
        for((index, gp) in gpoints.withIndex()){
            val polygon = Polygon()

            for(t in 0..36){
                polygon.addPoint(calculatePointInCircle(gp.latitude,gp.longitude,accuracies[index],t,36))

            }
            polygon.addPoint(GeoPoint(calculatePointInCircle(gp.latitude,gp.longitude,accuracies[index],0,36)))
            polygons.add(polygon)
            map?.overlayManager?.add(polygon)
            map?.invalidate()
        }
    }


    private fun calculatePointInCircle(lat: Double, lgn: Double, r: Float, t: Int, part: Int): GeoPoint{

        val earthRad = 6378137.0
        val radiansToDegrees = 180.0/ PI
        val rlat = r/earthRad*radiansToDegrees
        val rlng = r/(earthRad* cos(PI*lat/180.0))*radiansToDegrees
        return GeoPoint(lat+ rlat* cos(2*PI*t/part),lgn+rlng* sin(2*PI*t/part))
    }


}

interface ShowMenuFragmentDelegate{
    fun showMenuFragment(fragment: MapFragment)
}