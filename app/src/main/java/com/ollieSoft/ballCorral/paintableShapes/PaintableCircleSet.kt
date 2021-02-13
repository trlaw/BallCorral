package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory

class PaintableCircleList(): ArrayList<PaintableCircle>(), PaintableShape {

    override fun paintShape(paintCanvas: Canvas, paintFact: AbstractPaintFactory?) {
        this.forEach { it.paintShape(paintCanvas,paintFact) }
    }

}