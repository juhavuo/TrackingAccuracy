package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.graphics.DashPathEffect
import android.graphics.PathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * This class is for representation of readily gathered location data in map fragment.
 * The measured values together with accuracies can be shown as well as
 */
class MapFragment : Fragment() {

    private var map: MapView? = null
    private lateinit var nameLabelsRecyclerView: RecyclerView
    private lateinit var mapFragmentNameListingAdapter: MapFragmentNameListingAdapter
    private val routeLabels: ArrayList<RouteLabel> = ArrayList()
    private var mapFragmentJson: MapFragmentJson? = null
    private var dataAnalyzer: DataAnalyzer? = null
    private var delegate: ShowMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler
    private var isZoomed = false
    private var zoomLevel = 15.0


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mapPreferencesHandler = MapPreferencesHandler(context)

        //for using MenuFragment
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
        val linearLayoutManager = LinearLayoutManager(context)
        mapFragmentNameListingAdapter = MapFragmentNameListingAdapter(routeLabels,requireContext())
        nameLabelsRecyclerView = view.findViewById(R.id.map_fragment_name_label_rv)
        nameLabelsRecyclerView.layoutManager = linearLayoutManager
        nameLabelsRecyclerView.adapter = mapFragmentNameListingAdapter
        map = view.findViewById<MapView>(R.id.map_fragment_map)
        map?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
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
        /*
        shareButton.setOnClickListener {
            if(mapFragmentJson != null){
                val jsonSender = JsonSender(requireContext())
                jsonSender.createJsonFromCalculatedValues(mapFragmentJson!!)
                jsonSender.convertToFile()
                jsonSender.sendDataAsIntent()
            }
        }*/
        return view
    }

    //used when DataAnalyzer has loaded the data from database
    fun setDataAnalyzer(da: DataAnalyzer) {
        dataAnalyzer = da
    }

    override fun onStart() {
        super.onStart()
        if (dataAnalyzer != null) {

            if (dataAnalyzer!!.getAmountOfRoutes() > 0) {
                val extremes = getExtremes()
                setZoom(extremes)
                setCenter(extremes)

                if (mapPreferencesHandler.getAccuracyPreference()) {
                    //drawAccuraciesAsCircles();
                }
                if (mapPreferencesHandler.getBearingsPreference()) {
                    //drawBearings(geoPoints, dataAnalyzer!!.getBearings(),0.0003f)
                }
                drawPaths()
            }
            map?.invalidate()
        }
    }

    private fun setCenter(extremes: Array<Double>) {
        val centerpoint = GeoPoint((extremes[0] + extremes[1]) / 2, (extremes[2] + extremes[3]) / 2)
        map?.controller?.setCenter(centerpoint)
    }

    private fun setZoom(extremes: Array<Double>) {
        //the if statement needs to change eventually so it is if spans are more than some threshold to be determined
        if (extremes[0] != extremes[1] && extremes[2] != extremes[3]) {
            val latitudeSpan = abs(extremes[1] - extremes[0])
            val longitudeSpan = abs(extremes[3] - extremes[2])
            map?.controller?.zoomToSpan(latitudeSpan, longitudeSpan)
        } else {
            map?.controller?.setZoom(17.0)
        }
    }

    private fun getExtremes(): Array<Double> {

        val locationArray = dataAnalyzer!!.getGeoPointsAsArray()

        var latitudeMin = locationArray[0][0].latitude
        var latitudeMax = locationArray[0][0].latitude
        var longitudeMin = locationArray[0][0].longitude
        var longitudeMax = locationArray[0][0].longitude

        for (i in locationArray.indices) {
            for (j in locationArray[i].indices) {
                if (locationArray[i][j].latitude < latitudeMin) {
                    latitudeMin = locationArray[i][j].latitude
                } else if (locationArray[i][j].latitude > latitudeMax) {
                    latitudeMax = locationArray[i][j].latitude
                }

                if (locationArray[i][j].longitude < longitudeMin) {
                    longitudeMin = locationArray[i][j].longitude
                } else if (locationArray[i][j].longitude > longitudeMax) {
                    longitudeMax = locationArray[i][j].longitude
                }
            }
        }
        return arrayOf(latitudeMin, latitudeMax, longitudeMin, longitudeMax)
    }

    /*
        Draw the accuracies to the map
     */
    private fun drawAccuraciesAsCircles(
        gpoints: ArrayList<GeoPoint>,
        accuracies: ArrayList<Float>
    ) {
        for (index in 0 until gpoints.size) {
            val circlePoints = Polygon.pointsAsCircle(gpoints[index], accuracies[index].toDouble())
            val polygon = Polygon()
            polygon.outlinePaint.color = resources.getColor(R.color.colorAccuracyCircle, null)
            for (point in circlePoints) {
                polygon.addPoint(point)
            }
            map?.overlayManager?.add(polygon)
            map?.invalidate()
        }
    }

    /*
        For now the zoom level is stored in shared preferences
        when exiting MapFragment
     */
    override fun onDestroyView() {
        super.onDestroyView()
        if (isZoomed) {
            mapPreferencesHandler.storeMapZoomPreference(zoomLevel)
        }
    }


    /*
        Draws the path according to both measured locations and calculated locations
        using algorithms
     */
    private fun drawPaths() {
        val amoutOfPreferences = mapPreferencesHandler.getAmoutOfAlgorithmPreferences()
        val polylines = arrayOfNulls<Polyline>(amoutOfPreferences)
        val mappedRouteJsons: ArrayList<MappedRouteJson> = ArrayList()
        var points: ArrayList<GeoPoint> = ArrayList()

        var amountOfRoutes = 0
        if(dataAnalyzer !=null) {
            amountOfRoutes = dataAnalyzer!!.getAmountOfRoutes()
            val names = dataAnalyzer!!.getNamesOfRoutes()
            for (j in 0 until amountOfRoutes) {
                for (i in 0 until amoutOfPreferences) {
                    polylines[i] = Polyline()
                    if (mapPreferencesHandler.getAlgorithmPreference(i)) {
                        var parameterData: String? = null
                        when (i) {
                            0 -> {
                                polylines[0]?.outlinePaint?.color =
                                    resources.getColor(R.color.colorMeasuredPolyline, null)
                                points = dataAnalyzer!!.getMeasuredLocationsAsGeoPoints(j)
                                polylines[0]?.setPoints(points)

                            }
                            1 -> {
                                polylines[1]?.outlinePaint?.color =
                                    resources.getColor(R.color.colorAlgorithm1Polyline, null)
                                val epsilon = mapPreferencesHandler.getEpsilonPreference()

                                points = dataAnalyzer!!.getAlgorithm1GeoPoints(j, epsilon)
                                polylines[1]?.setPoints(points)
                                parameterData = "{\"epsilon\":$epsilon}"

                            }
                            2 -> {
                                polylines[2]?.outlinePaint?.color =
                                    resources.getColor(R.color.colorAlgorithm2Polyline, null)

                                points = dataAnalyzer!!.getKalmanFilteredGeoPoints(j)
                                polylines[2]?.setPoints(points)

                            }
                            3 -> {
                                polylines[3]?.outlinePaint?.color =
                                    resources.getColor(R.color.colorAlgorithm3Polyline, null)
                                val accuracyThSeekbarValue =
                                    mapPreferencesHandler.getAccuracyThresholdPreference()

                                val accuracyThreshold =
                                    dataAnalyzer!!.calculateAccuracyFromBarReading(
                                        accuracyThSeekbarValue, 1000
                                    )
                                points = dataAnalyzer!!.getRemainingLocations(j, accuracyThreshold)
                                polylines[3]?.setPoints(points)
                                parameterData = "{\"accuracy_threshold\":$accuracyThreshold}"

                            }
                            4 -> {
                                polylines[4]?.outlinePaint?.color =
                                    resources.getColor(R.color.colorAlgorithm4Polyline, null)
                                val amountOfPoints =
                                    mapPreferencesHandler.getRunningMeanPreference()

                                val isWeighted = mapPreferencesHandler.getUseWeightsPreference()
                                points =
                                    dataAnalyzer!!.getMovingAverages(amountOfPoints, isWeighted)
                                polylines[4]?.setPoints(points)
                                parameterData =
                                    "{\"is_weigthed\":$isWeighted, \"amount_of_points\":$amountOfPoints}"
                            }
                        }

                        for (line in polylines) {
                            line?.outlinePaint?.pathEffect = selectDrawStyle(j)
                        }
                        routeLabels.add(RouteLabel(names[j],selectDrawStyle(j)))

                        mappedRouteJsons.add(MappedRouteJson(i, parameterData, points))
                        points.clear()
                        map?.overlayManager?.add(polylines[i])
                    }
                }
            }
            Log.i("routeLabels","${routeLabels.size}")
            mapFragmentNameListingAdapter.updateLabels(routeLabels)
        }
        map?.invalidate()

        /*
        if(routeId!=null && routeName!=null){
            mapFragmentJson = MapFragmentJson(routeId!!,routeName!!,mappedRouteJsons)
        }*/
    }

    private fun selectDrawStyle(index: Int): PathEffect {
        var pathEffect: PathEffect
        when (index) {
            0 -> pathEffect = PathEffect()
            1 -> pathEffect = DashPathEffect(floatArrayOf(40f, 10f), 0f)
            2 -> pathEffect = DashPathEffect(floatArrayOf(10f, 10f, 20f, 10f), 0f)
            3 -> pathEffect = DashPathEffect(floatArrayOf(20f, 20f, 5f, 20f), 0f)
            4 -> pathEffect = DashPathEffect(floatArrayOf(4f, 4f, 8f, 4f), 0f)
            else -> pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
        }
        return pathEffect
    }

    /*
        Draws bearings as lines pointing from location to the direction of bearing,
        parameter r: the length of line
     */
    private fun drawBearings(gpoints: ArrayList<GeoPoint>, bearings: ArrayList<Float>, r: Float) {

        for ((index, gp) in gpoints.withIndex()) {
            val linePoints: ArrayList<GeoPoint> = ArrayList()
            linePoints.add(gp)
            val latitude = gp.latitude + r * cos(bearings[index] * PI / 180.0)
            val longitude = gp.longitude + r * sin(bearings[index] * PI / 180.0)
            linePoints.add(GeoPoint(latitude, longitude))
            val polyline = Polyline()
            polyline.outlinePaint.color = resources.getColor(R.color.colorAccent, null)
            polyline.setPoints(linePoints)
            map?.overlayManager?.add(polyline)
        }
    }


}

interface ShowMenuFragmentDelegate {
    fun showMenuFragment(fragment: MapFragment)
}