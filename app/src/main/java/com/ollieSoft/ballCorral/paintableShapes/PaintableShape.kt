package com.ollieSoft.ballCorral.paintableShapes

import android.graphics.Canvas
import android.graphics.Paint
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory

interface PaintableShape {

    fun paintShape(paintCanvas: Canvas,paintFact: AbstractPaintFactory?)


}