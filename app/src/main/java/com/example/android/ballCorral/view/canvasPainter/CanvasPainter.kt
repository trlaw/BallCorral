package com.example.android.ballCorral.view.canvasPainter

import android.graphics.Canvas
import com.example.android.ballCorral.utility.Rectangle
import com.example.android.ballCorral.view.CanvasDrawView

abstract class CanvasPainter {
    var delegatePainter: CanvasPainter? = null
    abstract fun paintViewCanvas(canvas: Canvas,view: CanvasDrawView)
    abstract fun getContentBounds(): Rectangle?
}