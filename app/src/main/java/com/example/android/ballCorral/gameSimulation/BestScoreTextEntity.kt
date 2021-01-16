package com.example.android.ballCorral.gameSimulation

import com.example.android.ballCorral.paintableShapes.PaintableShape
import com.example.android.ballCorral.utility.Vector

const val BEST_SCORE_TEXT_X = 480f
const val BEST_SCORE_TEXT_Y = 60f

class BestScoreTextEntity : TextEntity(Vector(BEST_SCORE_TEXT_X, BEST_SCORE_TEXT_Y)) {
    var bestScore = 0
    override fun getPaintableShape(): PaintableShape {
        return super.getPaintableShape().apply {
            text = "Best: $bestScore"
        }
    }
}