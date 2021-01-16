package com.example.android.ballCorral.paintableShapes

import android.graphics.Canvas
import com.example.android.ballCorral.utility.Vector
import com.example.android.ballCorral.view.TextPaintFactory

class PaintableText(val text:String,val position: Vector): PaintableShape() {
    lateinit var textPaintFactory: TextPaintFactory

    override fun paintShape(paintCanvas: Canvas) {
        paintCanvas.drawText(text,position.x,position.y,textPaintFactory.getPaint())
    }
}