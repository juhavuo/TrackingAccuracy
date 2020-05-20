package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class MenuFragment: Fragment(){

    private var delegate: CloseMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler
    private var dataAnalyzer: DataAnalyzer? = null

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
        val view = inflater.inflate(R.layout.fragment_menu,container,false)
        val accuraciesCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_accuracies_checkBox)
        accuraciesCheckBox.isChecked = mapPreferencesHandler.getAccuracyPreference()
        accuraciesCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mapPreferencesHandler.storeAccuracyPreference(isChecked)
        }

        val bearingsCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_bearings_checkbox)
        bearingsCheckBox.isChecked = mapPreferencesHandler.getBearingsPreference()
        bearingsCheckBox.setOnCheckedChangeListener{_, isChecked ->
            mapPreferencesHandler.storeBearingsPreference(isChecked)
        }

        val showMeasuredCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_show_measured_checkbox)
        showMeasuredCheckBox.isChecked = mapPreferencesHandler.getAlgorithmPreference(0)
        showMeasuredCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mapPreferencesHandler.storeAlgorithmPreference(0,isChecked)
        }

        val algorithmCheckBoxes: ArrayList<CheckBox> = ArrayList()
        algorithmCheckBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_1_checkbox))
        algorithmCheckBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_2_checkbox))
        algorithmCheckBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_3_checkbox))
        for((index, aCheckBox) in algorithmCheckBoxes.withIndex()){
            aCheckBox.isChecked = mapPreferencesHandler.getAlgorithmPreference(index+1)
            aCheckBox.setOnCheckedChangeListener{_, isCheked ->
                mapPreferencesHandler.storeAlgorithmPreference(index+1,isCheked)
            }
        }

        val epsilonTextView = view.findViewById<TextView>(R.id.menu_fragment_epsilon_value)
        val epsilonSeekBar = view.findViewById<SeekBar>(R.id.menu_fragment_epsilon_seekbar)
        val epsilonMultiplier = 10000.0
        val epsilonBegin = mapPreferencesHandler.getEpsilonPreference()
        epsilonSeekBar.progress = (epsilonBegin*epsilonMultiplier*1000).toInt()
        epsilonTextView.text = epsilonBegin.toString()
        epsilonSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val epsilon = progress/(epsilonMultiplier*1000)
                epsilonTextView.text = epsilon.toString()
                mapPreferencesHandler.storeEpsilonPreference(epsilon)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

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
                        mapPreferencesHandler.storeAccuracyTresholdPreference(value)
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })

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

interface CloseMenuFragmentDelegate{
    fun closeMenuFragment()
}