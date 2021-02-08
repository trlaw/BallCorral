package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory
import com.ollieSoft.ballCorral.view.paintFactory.TextPaintFactory

class PaintableText(private val text: String, private val position: Vector) : PaintableShape() {

    override fun paintShape(paintCanvas: Canvas, paintFact: AbstractPaintFactory?) {
        if (paintFact is TextPaintFactory) {
            paintCanvas.drawText(
                text,
                position.x.toFloat(),
                position.y.toFloat(),
                paintFact.getPaint()
            )
        }
    }

}