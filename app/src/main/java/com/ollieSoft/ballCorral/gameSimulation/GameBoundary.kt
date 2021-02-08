package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.utility.Rectangle
import com.ollieSoft.ballCorral.utility.Vector
import kotlin.math.min
import kotlin.random.Random

const val BALL_INIT_V_MIN = 3f
const val BALL_INIT_V_MAX = 6f

class GameBoundary(dimensions: Vector) : Rectangle(Vector.zero(), dimensions) {

    val ballInitVmin = R.string.BALL_INIT_V_MIN.toDouble()
    val ballInitVmax = R.string.BALL_INIT_V_MAX.toDouble()

    fun getSpawnArea(): Rectangle {
        val offsetVector = Vector(width() / (4f), height() / (4f))
        return Rectangle(
            lowerBounds.plus(offsetVector),
            upperBounds.minus(offsetVector)
        )
    }

    fun setSpawnState(newBall: BallEntity) {
        newBall.apply {
            position = getRandomBallSpawnPosition()
            velocity = getRandomBallSpawnVelocity()
        }
    }

    private fun getRandomBallSpawnPosition(): Vector {
        return upperBounds.minus(lowerBounds).times(0.5).plus(
            lowerBounds.plus(
                Vector.randomUnit()
                    .times(0.25f * min(height(), width()))
            )
        )
    }

    private fun getRandomBallSpawnVelocity(): Vector {
        return Vector.randomUnit()
            .times(ballInitVmin + Random.nextDouble() * (ballInitVmax - ballInitVmin))
    }

}