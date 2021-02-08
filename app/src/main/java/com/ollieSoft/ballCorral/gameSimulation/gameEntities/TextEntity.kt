package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.paintableShapes.PaintableText
import com.ollieSoft.ballCorral.utility.Vector

open class TextEntity(private val position: Vector) : GameEntity(), PaintableEntity {
    var text = ""
    override fun getPaintableShape(): PaintableShape {
        return PaintableText(text, position)
    }
}