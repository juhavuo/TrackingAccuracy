package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class MenuFragment: Fragment(){

    private var delegate: CloseMenuFragmentDelegate? = null
    private lateinit var mapPreferencesHandler: MapPreferencesHandler

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

        val showLinesCheckBox = view.findViewById<CheckBox>(R.id.menu_fragment_show_lines_checkbox)
        showLinesCheckBox.isChecked = mapPreferencesHandler.getShowLinesPreference()
        showLinesCheckBox.setOnCheckedChangeListener { _, isChecked ->
            mapPreferencesHandler.storeShowLinesPreference(isChecked)
        }

        val algorithmChecBoxes: ArrayList<CheckBox> = ArrayList()
        algorithmChecBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_1_checkbox))
        algorithmChecBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_2_checkbox))
        algorithmChecBoxes.add(view.findViewById(R.id.menu_fragment_algorithm_3_checkbox))
        for((index, aCheckBox) in algorithmChecBoxes.withIndex()){
            aCheckBox.isChecked = mapPreferencesHandler.getAlgorithmPreference(index)
            aCheckBox.setOnCheckedChangeListener{_, isCheked ->
                mapPreferencesHandler.storeAlgorithmPreference(index,isCheked)
            }
        }

        val closeButton = view.findViewById<ImageButton>(R.id.menu_fragment_close_button)
        closeButton.setOnClickListener {
            delegate?.closeMenuFragment()
        }
        return view
    }

}

interface CloseMenuFragmentDelegate{
    fun closeMenuFragment()
}