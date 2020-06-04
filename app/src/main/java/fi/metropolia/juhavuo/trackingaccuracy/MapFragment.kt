package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
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
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
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
    private lateinit var lengths_listing_view: TextView
    private var dataAnalyzer: DataAnalyzer? = null
    private var delegate: ShowMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler
    private val polygons: ArrayList<Polygon> = ArrayList()
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
            title.text = getString(R.string.map_fragment_title,routeName!!)
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
        return view
    }

    fun setDataAnalyzer(da: DataAnalyzer) {
        dataAnalyzer = da
    }

    fun setRouteName(rName: String){
        routeName = rName
    }

    override fun onStart() {
        super.onStart()


        val showAccuracies = mapPreferencesHandler.getAccuracyPreference()
        Log.i("test", "map fragment on start, accuracies show: $showAccuracies")
        if (dataAnalyzer != null) {
            val geoPoints = dataAnalyzer!!.getMeasuredLocationsAsGeoPoints()
            if (geoPoints.isNotEmpty()) {
                map?.controller?.setZoom(mapPreferencesHandler.getMapZoomPreference())
                map?.controller?.setCenter(geoPoints[0])

                if (mapPreferencesHandler.getAccuracyPreference()) {
                    constructPolygons(geoPoints, dataAnalyzer!!.getAccuracies())
                }
                if (mapPreferencesHandler.getBearingsPreference()){
                    drawBearings(geoPoints, dataAnalyzer!!.getBearings(),0.0003f)
                }
                drawPaths()

            }

            map?.invalidate()
        }

    }

    private fun constructPolygons(gpoints: ArrayList<GeoPoint>, accuracies: ArrayList<Float>) {
        for ((index, gp) in gpoints.withIndex()) {
            val polygon = Polygon()
            val dividedInto = 36

            for (t in 0..dividedInto) {
                polygon.addPoint(
                    calculatePointInCircle(
                        gp.latitude,
                        gp.longitude,
                        accuracies[index],
                        t,
                        dividedInto
                    )
                )

            }
            polygon.addPoint(
                GeoPoint(
                    calculatePointInCircle(
                        gp.latitude,
                        gp.longitude,
                        accuracies[index],
                        0,
                        dividedInto
                    )
                )
            )
            polygons.add(polygon)
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


    private fun calculatePointInCircle(
        lat: Double,
        lgn: Double,
        r: Float,
        t: Int,
        part: Int
    ): GeoPoint {

        val earthRad = 6378137.0
        val radiansToDegrees = 180.0 / PI
        val rlat = r / earthRad * radiansToDegrees
        val rlng = r / (earthRad * cos(PI * lat / 180.0)) * radiansToDegrees
        return GeoPoint(lat + rlat * cos(2 * PI * t / part), lgn + rlng * sin(2 * PI * t / part))
    }

    private fun drawPaths() {

        val amoutOfPreferences = mapPreferencesHandler.getAmoutOfAlgorithmPreferences()
        val polylines = arrayOfNulls<Polyline>(amoutOfPreferences)
        val lengthListings: ArrayList<String> = ArrayList()
        for (i in 0 until amoutOfPreferences) {
            polylines[i] = Polyline()
            if (mapPreferencesHandler.getAlgorithmPreference(i)) {
                when (i) {
                    0 -> {
                        polylines[0]?.outlinePaint?.color =
                            resources.getColor(R.color.colorMeasuredPolyline, null)
                        if (dataAnalyzer != null) {
                            val points = dataAnalyzer!!.getMeasuredLocationsAsGeoPoints()
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
                            val points = dataAnalyzer!!.getAlgorithm1GeoPoints(epsilon)
                            polylines[1]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("1: $length m")
                        }
                    }
                    2 -> {
                        polylines[2]?.outlinePaint?.color =
                            resources.getColor(R.color.colorAlgorithm2Polyline, null)
                        if (dataAnalyzer != null) {
                            val points = dataAnalyzer!!.getKalmanFilteredGeoPoints()
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
                            val points = dataAnalyzer!!.getRemainingLocations(accuracyThreshold)
                            polylines[3]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("3: $length m")
                        }
                    }
                    4 -> {
                        polylines[4]?.outlinePaint?.color =
                            resources.getColor(R.color.colorAlgorithm4Polyline,null)
                        val amountOfPoints = mapPreferencesHandler.getRunningMeanPreference()
                        if(dataAnalyzer!=null){
                            val points = dataAnalyzer!!.getMovingAverages(amountOfPoints,mapPreferencesHandler.getUseWeightsPreference())
                            polylines[4]?.setPoints(points)
                            val length = dataAnalyzer!!.getLengthOfRoute(points)
                            lengthListings.add("4: $length m")
                        }
                    }

                }
                map?.overlayManager?.add(polylines[i])
            }
            map?.invalidate()
        }
        var lengthtext = ""
        for((index,l) in lengthListings.withIndex()){
            lengthtext += l
            if(index<lengthListings.size-1){
                lengthtext+=", "
            }
        }
        lengths_listing_view.text=lengthtext
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