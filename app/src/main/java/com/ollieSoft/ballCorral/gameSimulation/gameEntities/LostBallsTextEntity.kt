package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.utility.Vector

const val LOST_BALL_TEXT_X = 40f
const val LOST_BALL_TEXT_Y = 60f

class LostBallsTextEntity(): TextEntity(Vector(LOST_BALL_TEXT_X, LOST_BALL_TEXT_Y)) {
    var lostBallCount = 0
    override fun getPaintableShape(): PaintableShape {
        return super.getPaintableShape().apply {
            text = "Lost: $lostBallCount"
        }
    }
}