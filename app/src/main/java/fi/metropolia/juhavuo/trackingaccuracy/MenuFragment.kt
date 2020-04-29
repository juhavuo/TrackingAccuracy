package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class MenuFragment: Fragment(){

    private var delegate: CloseMenuFragmentDelegate? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
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