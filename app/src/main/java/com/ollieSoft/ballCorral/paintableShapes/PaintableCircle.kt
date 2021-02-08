package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory
import com.ollieSoft.ballCorral.view.paintFactory.CirclePaintFactory

class PaintableCircle(
    private val center: Vector,
    private val radius: Double,
    private val colorIndex: Int = 0
) :
    PaintableShape() {

    override fun paintShape(paintCanvas: Canvas, paintFact: AbstractPaintFactory?) {
        if (paintFact is CirclePaintFactory) {
            paintCanvas.drawCircle(
                center.x.toFloat(),
                center.y.toFloat(),
                radius.toFloat(),
                paintFact.getFillPaint(colorIndex)
            )

            paintCanvas.drawCircle(
                center.x.toFloat(),
                center.y.toFloat(),
                (radius - paintFact.getOutlinePaint().strokeWidth / 2).toFloat(),
                paintFact.getOutlinePaint()
            )
        }
    }
}