package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory

interface PaintableShape {
    fun paintShape(paintCanvas: Canvas,paintFact: AbstractPaintFactory?)
}