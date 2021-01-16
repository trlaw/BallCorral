package com.example.android.ballCorral.gameSimulation

import com.example.android.ballCorral.paintableShapes.PaintableShape

interface PaintableEntity {
    fun getPaintableShape(): PaintableShape
}