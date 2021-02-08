package com.ollieSoft.ballCorral.view.paintFactory

import android.graphics.Paint
import com.ollieSoft.ballCorral.R

const val OUTLINE_WIDTH = 3f

class CirclePaintFactory(private val ballColors: IntArray, private val strokeColor: Int) :
    AbstractPaintFactory() {

    private var ballPaints: Array<Paint> = Array<Paint>(ballColors.size) { i ->
        Paint().apply {
            color = ballColors[i]
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
        }
    }

    private var outlinePaint: Paint = Paint().apply {
        color = strokeColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = R.string.CIRCLE_OUTLINE_WIDTH.toFloat()
    }

    fun getOutlinePaint(): Paint {
        return outlinePaint
    }

    fun getFillPaint(colorIndex: Int): Paint {
        return ballPaints[colorIndex.rem(ballColors.size)]
    }
}