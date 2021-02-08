package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.utility.Vector

class ScoreEntity :
    TextEntity(Vector(R.string.SCORE_TEXT_X.toDouble(), R.string.SCORE_TEXT_Y.toDouble())) {

    var score: Long = 0

    override fun getPaintableShape(): PaintableShape {
        return super.getPaintableShape().apply {
            text = "Score: $score"
        }
    }

}