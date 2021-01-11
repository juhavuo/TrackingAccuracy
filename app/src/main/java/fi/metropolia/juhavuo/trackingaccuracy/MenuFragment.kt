package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import java.lang.NumberFormatException

class MenuFragment: Fragment(){

    private var delegate: CloseMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler
    private var dataAnalyzer: DataAnalyzer? = null
    private var routes: ArrayList<Route> = ArrayList()
    private var routeNames: ArrayList<String> = ArrayList()
    private var routes_index = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mapPreferencesHandler = MapPreferencesHandler(context)

        if(context is CloseMenuFragmentDelegate){
            delegate = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //CheckBox to show accuracies in MapView
        val view = inflater.inflate(R.layout.fragment_menu,container,false)

        routes = dataAnalyzer!!.getRoutes()
        for(route in routes){
            routeNames.add(route.name)
        }

        val spinner = view.findViewById<Spinner>(R.id.menu_fragment_select_route_spinner)
        val arrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,routeNames)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, spinner_id: Long) {
                routes_index = position
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        val accuraciesCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_accuracies_checkBox)
        accuraciesCheckBox.isChecked = mapPreferencesHandler.getAccuracyPreference()
        accuraciesCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mapPreferencesHandler.storeAccuracyPreference(isChecked)
        }

        //CheckBox to show bearings in MapView
        val bearingsCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_bearings_checkbox)
        bearingsCheckBox.isChecked = mapPreferencesHandler.getBearingsPreference()
        bearingsCheckBox.setOnCheckedChangeListener{_, isChecked ->
            mapPreferencesHandler.storeBearingsPreference(isChecked)
        }

        //CheckBox to show original unfiltered locations in MapView
        val showMeasuredCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_show_measured_checkbox)
        showMeasuredCheckBox.isChecked = mapPreferencesHandler.getAlgorithmPreference(0)
        showMeasuredCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mapPreferencesHandler.storeAlgorithmPreference(0,isChecked)
        }

        //CheckBoxes to show paths calculated using different algororithms in MapView
        val algorithmCheckBoxes: ArrayList<CheckBox> = ArrayList()
        algorithmCheckBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_1_checkbox))
        algorithmCheckBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_2_checkbox))
        algorithmCheckBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_3_checkbox))
        algorithmCheckBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_4_checkbox))
        for((index, aCheckBox) in algorithmCheckBoxes.withIndex()){
            aCheckBox.isChecked = mapPreferencesHandler.getAlgorithmPreference(index+1)
            aCheckBox.setOnCheckedChangeListener{_, isCheked ->
                mapPreferencesHandler.storeAlgorithmPreference(index+1,isCheked)
            }
        }

        //Set up of SeekBar for Ramer-Douglas-Peucker algorithm to adjust epsilon value
        val epsilonTextView = view.findViewById<TextView>(R.id.menu_fragment_epsilon_value)
        val epsilonSeekBar = view.findViewById<SeekBar>(R.id.menu_fragment_epsilon_seekbar)
        val epsilonMultiplier = 1000.0*2000
        val epsilonBegin = mapPreferencesHandler.getEpsilonPreference()
        epsilonSeekBar.progress = (epsilonBegin*epsilonMultiplier).toInt()
        epsilonTextView.text = epsilonBegin.toString()
        epsilonSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val epsilon = progress/(epsilonMultiplier)
                epsilonTextView.text = epsilon.toString()
                //mapPreferencesHandler.storeEpsilonPreference(epsilon)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        //Seekbar for remove most inaccurate locations -algorithm:
        val accuracySeekbar = view.findViewById<SeekBar>(R.id.menu_fragment_accuracy_threshold_seekbar)
        val accuracyThresholdTextView = view.findViewById<TextView>(R.id.menu_fragment_accuracy_threshold_value_textview)
        val locationsRemovedTextView = view.findViewById<TextView>(R.id.menu_fragment_locations_removed_textview)
        var accuracyThSeekbarReading = mapPreferencesHandler.getAccuracyThresholdPreference()
        var accuracyThreshold = 0f
        var extremes = floatArrayOf(0f,0f)
        var amountOfLocations = 0
        var pointsToRemoved: Int
        val sliderMax = 1000
        if(dataAnalyzer!=null){
            extremes = dataAnalyzer!!.getAccuracyExtremes()
            accuracyThreshold = dataAnalyzer!!.calculateAccuracyFromBarReading(accuracyThSeekbarReading,sliderMax)
            accuracySeekbar.progress = accuracyThSeekbarReading
            if(extremes[1] > 0 && extremes[1] > extremes[0]){
                amountOfLocations = dataAnalyzer!!.getAmountOfLocations()
                pointsToRemoved = dataAnalyzer!!.getAmountOfPointsToBeRemoved(accuracyThreshold)
                accuracyThresholdTextView.text = accuracyThreshold.toString()
                locationsRemovedTextView.text = resources.getString(R.string.menu_fragment_locations_removed_textview,pointsToRemoved,amountOfLocations)
            }
        }

        accuracySeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                if(extremes[1] > 0 && extremes[1] > extremes[0]){
                    if(dataAnalyzer!=null){
                        accuracyThreshold = dataAnalyzer!!.calculateAccuracyFromBarReading(value,sliderMax)
                        accuracyThresholdTextView.text = accuracyThreshold.toString()
                        pointsToRemoved = dataAnalyzer!!.getAmountOfPointsToBeRemoved(accuracyThreshold)
                        locationsRemovedTextView.text = resources.getString(R.string.menu_fragment_locations_removed_textview,pointsToRemoved,amountOfLocations)
                        mapPreferencesHandler.storeAccuracyThresholdPreference(value)
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

        //For running average algorithm, there is TextView for input of
        // from how many values the average is calculated
        val algorithm4EditText = view.findViewById<EditText>(R.id.menu_fragment_algorithm4_edit_text)
        algorithm4EditText.setOnEditorActionListener { view, actionId, event ->
            if(actionId==EditorInfo.IME_ACTION_DONE){
                try{
                    val amount = algorithm4EditText.text.toString().toInt()
                    mapPreferencesHandler.storeRunningMeanPreference(amount)
                }catch (exception: NumberFormatException){
                    Log.i("textedit","$exception")
                }
            }
            false
        }

        //Checkbox for using weights in running average algorithm
        val useWeightsCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_include_weights_checkbox)
        useWeightsCheckBox.isChecked = mapPreferencesHandler.getUseWeightsPreference()
        useWeightsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mapPreferencesHandler.storeUseWeightsPreference(isChecked)
        }

        //closing MenuFragment brings back to MapFragment
        val closeButton = view.findViewById<ImageButton>(R.id.menu_fragment_close_button)
        closeButton.setOnClickListener {
            delegate?.closeMenuFragment()
        }
        return view
    }

    fun getDataAnalyzer(da: DataAnalyzer){
        dataAnalyzer = da
    }

}



//for closing MenuFragment and return to MapFragment
interface CloseMenuFragmentDelegate{
    fun closeMenuFragment()
}