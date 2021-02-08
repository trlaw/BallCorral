package com.ollieSoft.ballCorral.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ollieSoft.ballCorral.utility.Rectangle
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.view.canvasPainter.CanvasPainter
import com.ollieSoft.ballCorral.view.userInput.LineTouchManager


class CanvasDrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var framePainter: CanvasPainter? = null
    var touchEventHandler: ((MotionEvent) -> Unit)? = null
    var sizeChangeCallback: ((Int, Int) -> Unit)? = null
    var drawingSpace = Rectangle(Vector.zero(), Vector.zero())
    var lineTouchManager: LineTouchManager? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingSpace = Rectangle(Vector.zero(),Vector(w.toDouble(),h.toDouble()))
        sizeChangeCallback?.invoke(w, h)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        super.onTouchEvent(e)
        lineTouchManager?.handleTouchEvent(e)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (framePainter != null) {
            framePainter!!.paintViewCanvas(canvas,this)
        }
    }
}

