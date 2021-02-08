package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory

abstract class PaintableShape() {
    open fun paintShape(paintCanvas: Canvas,paintFact: AbstractPaintFactory?) {

    }
}