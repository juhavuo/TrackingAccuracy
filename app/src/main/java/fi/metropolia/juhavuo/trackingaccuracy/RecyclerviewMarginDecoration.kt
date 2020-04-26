package fi.metropolia.juhavuo.trackingaccuracy

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//https://medium.com/@elye.project/right-way-of-setting-margin-on-recycler-views-cell-319da259b641
class RecyclerviewMarginDecoration (private val margin: Int):RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect){
            bottom = margin
        }
    }

}