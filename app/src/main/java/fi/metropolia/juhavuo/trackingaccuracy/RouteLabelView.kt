package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class RouteLabelView: View{

    private var pathEffect: PathEffect? = null
    private val paint: Paint = Paint()
    private var viewSize: Float

    constructor(context: Context, attrs: AttributeSet):super(context, attrs){
        paint.color = Color.BLACK
        paint.strokeWidth = 3f
        viewSize = context.resources.getDimension(R.dimen.name_label_view_size)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.pathEffect = pathEffect
        canvas.drawPath(makePath(),paint)
    }

    private fun makePath(): Path{
        val path = Path()
        path.moveTo(0F,viewSize/2)
        path.lineTo(viewSize, viewSize/2)
        path.close()
        return path
    }

    fun setPathEffect(pEffect: PathEffect){
        pathEffect = pEffect
        invalidate()
    }
}