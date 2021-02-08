package com.ollieSoft.ballCorral.view.canvasPainter

import android.graphics.Canvas
import com.ollieSoft.ballCorral.utility.Rectangle
import com.ollieSoft.ballCorral.view.CanvasDrawView

abstract class CanvasPainter {
    var delegatePainter: CanvasPainter? = null
    abstract fun paintViewCanvas(canvas: Canvas,view: CanvasDrawView)
    abstract fun getContentBounds(): Rectangle?
}