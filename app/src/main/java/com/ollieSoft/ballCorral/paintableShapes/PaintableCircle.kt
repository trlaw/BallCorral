package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import android.graphics.Paint
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory
import com.ollieSoft.ballCorral.view.paintFactory.CirclePaintFactory

class PaintableCircle(
    private val center: Vector,
    private val radius: Double,
    private val colorIndex: Int = 0, private var opacity: Int = 255
) :
    PaintableShape {

    override fun paintShape(paintCanvas: Canvas, paintFact: AbstractPaintFactory?) {
        if (paintFact is CirclePaintFactory) {
            val fillPaint = paintFact.getFillPaint(colorIndex)
            swapOpacity(fillPaint)
            paintCanvas.drawCircle(
                center.x.toFloat(),
                center.y.toFloat(),
                radius.toFloat(),
                paintFact.getFillPaint(colorIndex)
            )
            swapOpacity(fillPaint)

            val outlinePaint = paintFact.getOutlinePaint()
            swapOpacity(outlinePaint)
            paintCanvas.drawCircle(
                center.x.toFloat(),
                center.y.toFloat(),
                (radius - paintFact.getOutlinePaint().strokeWidth / 2).toFloat(),
                paintFact.getOutlinePaint()
            )
            swapOpacity(outlinePaint)
        }
    }

    private fun swapOpacity(paint: Paint) {
        if (opacity != paint.alpha) {
            val tempOpacity = paint.alpha
            paint.alpha = opacity
            opacity = tempOpacity
        }
    }
}