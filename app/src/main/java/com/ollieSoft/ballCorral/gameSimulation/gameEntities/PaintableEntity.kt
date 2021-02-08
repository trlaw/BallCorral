package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.paintableShapes.PaintableShape

interface PaintableEntity {
    fun getPaintableShape(): PaintableShape
}