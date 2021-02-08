package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory
import com.ollieSoft.ballCorral.view.paintFactory.LinePaintFactory

class PaintableLine(private val start: Vector, private val end: Vector, val width: Double) :
    PaintableShape() {

    override fun paintShape(paintCanvas: Canvas, paintFact: AbstractPaintFactory?) {
        if (paintFact is LinePaintFactory) {
            paintCanvas.drawLine(
                start.x.toFloat(),
                start.y.toFloat(),
                end.x.toFloat(),
                end.y.toFloat(),
                paintFact.getPaint(width)
            )
        }
    }

}