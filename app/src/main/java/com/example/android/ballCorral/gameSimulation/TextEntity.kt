package com.example.android.ballCorral.gameSimulation

import com.example.android.ballCorral.paintableShapes.PaintableShape
import com.example.android.ballCorral.paintableShapes.PaintableText
import com.example.android.ballCorral.utility.Vector

open class TextEntity(private val position: Vector) : GameEntity(),PaintableEntity {
    var text = ""
    override fun getPaintableShape(): PaintableShape {
        return PaintableText(text, position)
    }
}