package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * This class is for representation of readily gathered location data in map fragment.
 * The measured values together with accuracies can be shown as well as
 */
class MapFragment : Fragment() {

    private var map: MapView? = null
    private var routeName: String? = null
    private var routeId: Int? = null
    private var mapFragmentJson: MapFragmentJson? = null
    private lateinit var lengths_listing_view: TextView
    private var dataAnalyzer: DataAnalyzer? = null
    private var delegate: ShowMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler
    private val polygons: ArrayList<Polygon> = ArrayList()
    private var averageAccuracy = 0.0
    private var isZoomed = false
    private var zoomLevel = 15.0


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mapPreferencesHandler = MapPreferencesHandler(context)

        if (context is ShowMenuFragmentDelegate) {
            delegate = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = context
        Configuration.getInstance().load(
            ctx,
            PreferenceManager.getDefaultSharedPreferences(ctx)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isZoomed = false


        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val title = view.findViewById<TextView>(R.id.map_fragment_title)
        if(routeName!=null){
            title.text = routeName!!

        }

        lengths_listing_view = view.findViewById(R.id.map_fragment_lengths_listing_textview)
        map = view.findViewById<MapView>(R.id.map_fragment_map)
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                if (event != null) {
                    zoomLevel = event.zoomLevel
                    isZoomed = true
                }
                return true
            }

        })
        val menubutton = view.findViewById<ImageButton>(R.id.map_fragment_menu_button)
        menubutton.setOnClickListener {
            delegate?.showMenuFragment(this)
        }
        val shareButton = view.findViewById<ImageButton>(R.id.map_fragment_share_button)
        shareButton.setOnClickListener {
            if(mapFragmentJson != null){
                val jsonSender = JsonSender(requireContext())
                jsonSender.createJsonFromCalculatedValues(mapFragmentJson!!)
                jsonSender.convertToFile()
                jsonSender.sendDataAsIntent()
            }
        }
        return view
    }

    fun setDataAnalyzer(da: DataAnalyzer) {
        dataAnalyzer = da
    }

    fun setRouteData(rId: Int, rName: String){
        routeId = rId
        routeName = rName
    }

    override fun onStart() {
        super.onStart()
        val showAccuracies = mapPreferencesHandler.getAccuracyPreference()
        Log.i("test", "map fragment on start, accuracies show: $showAccuracies")
        if (dataAnalyzer != null) {
            averageAccuracy = dataAnalyzer!!.getAverageAccuracy()
            Log.i("average","$averageAccuracy")
            map_fragment_average_accuracies_textview.text = resources.getString(R.string.map_fragment_average_accuracies,averageAccuracy)
            //map_fragment_average_accuracies_textview.text = "Accuracy: ${averageAccuracy}m"
            val geoPoints = dataAnalyzer!!.getMeasuredLocationsAsGeoPoints()
            if (geoPoints.isNotEmpty()) {
                map?.controller?.setZoom(mapPreferencesHandler.getMapZoomPreference())
                map?.controller?.setCenter(geoPoints[0])

                if (mapPreferencesHandler.getAccuracyPreference()) {
                    drawAccuraciesAsCircles(geoPoints,dataAnalyzer!!.getAccuracies())
                }
                if (mapPreferencesHandler.getBearingsPreference()){
                    drawBearings(geoPoints, dataAnalyzer!!.getBearings(),0.0003f)
                }
                drawPaths()

            }

            map?.invalidate()
        }

    }

    private fun drawAccuraciesAsCircles(gpoints: ArrayList<GeoPoint>, accuracies: ArrayList<Float>){
        for( index in 0 until gpoints.size){
            val circlePoints = Polygon.pointsAsCircle(gpoints[index],accuracies[index].toDouble())
            val polygon = Polygon()
            polygon.outlinePaint.color = resources.getColor(R.color.colorAccuracyCircle,null)
            for(point in circlePoints){
                polygon.addPoint(point)
            }
            map?.overlayManager?.add(polygon)
            map?.invalidate()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        if (isZoomed) {
            mapPreferencesHandler.storeMapZoomPreference(zoomLevel)
        }
    }

    
    private fun drawPaths() {
        val amoutOfPreferences = mapPreferencesHandler.getAmoutOfAlgorithmPreferences()
        val polylines = arrayOfNulls<Polyline>(amoutOfPreferences)
        val lengthListings: ArrayList<String> = ArrayList()
        val mappedRouteJsons: ArrayList<MappedRouteJson> = ArrayList()
        var points: ArrayList<GeoPoint> = ArrayList()
        for (i in 0 until amoutOfPreferences) {
            polylines[i] = Polyline()

            if (mapPreferencesHandler.getAlgorithmPreference(i)) {
                var parameterData: String? = null
                when (i) {
                    0 -> {
                        polylines[0]?.outlinePaint?.color =
                            resources.getColor(R.color.colorMeasuredPolyline, null)
                        if (dataAnalyzer != null) {
                            points = dataAnalyzer!!.getMeasuredLocationsAsGeoPoints()
                            polylines[0]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("0: $length m")
                        }

                    }
                    1 -> {
                        polylines[1]?.outlinePaint?.color =
                            resources.getColor(R.color.colorAlgorithm1Polyline, null)
                        val epsilon = mapPreferencesHandler.getEpsilonPreference()
                        if (dataAnalyzer != null) {
                            points = dataAnalyzer!!.getAlgorithm1GeoPoints(epsilon)
                            polylines[1]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("1: $length m")
                            parameterData = "{\"epsilon\":$epsilon}"
                        }
                    }
                    2 -> {
                        polylines[2]?.outlinePaint?.color =
                            resources.getColor(R.color.colorAlgorithm2Polyline, null)
                        if (dataAnalyzer != null) {
                            points = dataAnalyzer!!.getKalmanFilteredGeoPoints()
                            polylines[2]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("2: $length m")
                        }
                    }
                    3 -> {
                        polylines[3]?.outlinePaint?.color =
                            resources.getColor(R.color.colorAlgorithm3Polyline, null)
                        val accuracyThSeekbarValue =
                            mapPreferencesHandler.getAccuracyThresholdPreference()
                        if (dataAnalyzer != null) {
                            val accuracyThreshold = dataAnalyzer!!.calculateAccuracyFromBarReading(
                                accuracyThSeekbarValue, 1000)
                            points = dataAnalyzer!!.getRemainingLocations(accuracyThreshold)
                            polylines[3]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("3: $length m")
                            parameterData = "{\"accuracy_threshold\":$accuracyThreshold}"
                        }
                    }
                    4 -> {
                        polylines[4]?.outlinePaint?.color =
                            resources.getColor(R.color.colorAlgorithm4Polyline,null)
                        val amountOfPoints = mapPreferencesHandler.getRunningMeanPreference()
                        if(dataAnalyzer!=null){
                            val isWeighted = mapPreferencesHandler.getUseWeightsPreference()
                            points = dataAnalyzer!!.getMovingAverages(amountOfPoints,isWeighted)
                            polylines[4]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("4: $length m")
                            parameterData =  "{\"is_weigthed\":$isWeighted, \"amount_of_points\":$amountOfPoints}"

                        }
                    }

                }
                mappedRouteJsons.add(MappedRouteJson(i,parameterData,points))
                points.clear()
                map?.overlayManager?.add(polylines[i])
            }
        }
        map?.invalidate()
        var lengthtext = ""
        for((index,l) in lengthListings.withIndex()){
            lengthtext += l
            if(index<lengthListings.size-1){
                lengthtext+=", "
            }
        }
        lengths_listing_view.text=lengthtext

        if(routeId!=null && routeName!=null){
            mapFragmentJson = MapFragmentJson(routeId!!,routeName!!,mappedRouteJsons)
        }
    }

    private fun drawBearings(gpoints: ArrayList<GeoPoint>, bearings: ArrayList<Float>, r: Float){

        for ((index,gp) in gpoints.withIndex()){
            val linePoints: ArrayList<GeoPoint> = ArrayList()
            linePoints.add(gp)
            val latitude = gp.latitude+r* cos(bearings[index]*PI/180.0)
            val longitude = gp.longitude+r* sin(bearings[index]*PI/180.0)
            linePoints.add(GeoPoint(latitude,longitude))
            val polyline = Polyline()
            polyline.outlinePaint.color = resources.getColor(R.color.colorAccent,null)
            polyline.setPoints(linePoints)
            map?.overlayManager?.add(polyline)
        }
    }


}

interface ShowMenuFragmentDelegate {
    fun showMenuFragment(fragment: MapFragment)
}