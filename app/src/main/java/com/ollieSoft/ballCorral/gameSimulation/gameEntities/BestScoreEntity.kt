package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.utility.Vector

class BestScoreEntity : TextEntity(
    Vector(
        R.string.BEST_SCORE_TEXT_X.toDouble(),
        R.string.BEST_SCORE_TEXT_Y.toDouble()
    )
) {

    var bestScore: Long = 0

    override fun getPaintableShape(): PaintableShape {
        return super.getPaintableShape().apply {
            text = "Best: $bestScore"
        }
    }

}