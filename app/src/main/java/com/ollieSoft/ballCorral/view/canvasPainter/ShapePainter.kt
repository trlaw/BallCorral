package com.ollieSoft.ballCorral.view.canvasPainter

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.paintableShapes.*
import com.ollieSoft.ballCorral.utility.Rectangle
import com.ollieSoft.ballCorral.view.CanvasDrawView
import com.ollieSoft.ballCorral.view.paintFactory.AbstractPaintFactory
import com.ollieSoft.ballCorral.view.paintFactory.CirclePaintFactory
import com.ollieSoft.ballCorral.view.paintFactory.LinePaintFactory
import com.ollieSoft.ballCorral.view.paintFactory.TextPaintFactory

const val BACKGROUND_COLOR = Color.WHITE

class ShapePainter(resObj: Resources) : CanvasPainter() {

    private var circlePaintFactory: CirclePaintFactory = CirclePaintFactory(
        resObj.getIntArray(R.array.ballColors),
        resObj.getColor(R.color.colorForeground)
    )
    private var linePaintFactory = LinePaintFactory(resObj.getColor(R.color.colorForeground))
    private var textPaintFactory = TextPaintFactory()
    public var paintableShapeList: PaintableShapeList? = null

    override fun getContentBounds(): Rectangle? {
        if (paintableShapeList != null) {
            return Rectangle(
                paintableShapeList!!.shapesUpperLeft,
                paintableShapeList!!.shapesLowerRight
            )
        }
        return null
    }

    private fun getShapePaintFactory(paintableShape: PaintableShape): AbstractPaintFactory? {
        return when (paintableShape) {
            is PaintableCircle -> circlePaintFactory
            is PaintableLine -> linePaintFactory
            is PaintableText -> textPaintFactory
            else -> null
        }
    }

    override fun paintViewCanvas(canvas: Canvas, view: CanvasDrawView) {
        canvas.drawColor(BACKGROUND_COLOR)
        if (paintableShapeList != null) {
            for (paintableShape: PaintableShape in paintableShapeList!!) {
                paintableShape.paintShape(canvas,getShapePaintFactory(paintableShape))
            }
        }
    }


}